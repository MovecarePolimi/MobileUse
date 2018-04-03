package com.polimi.movecare_r01.ui;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.polimi.movecare_r01.R;
import com.polimi.movecare_r01.applicationLogic.service.LoginService;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private static final String BROADCAST_LOGIN_EVENT = "";

    private Context context;

    private EditText usernameText;
    private EditText passwordText;
    private Button loginButton;
    private ProgressDialog progressDialog;

    // Variables for testing
    private static final String username = "user101";
    private static final String password = "123456";
    private static final String grant_type = "password";
    private static final String application = "4bac09a5b8dd11e781af0242ac120002";
    private static final String tenant = "5a258444b8dd11e781af0242ac120002";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.context = getApplicationContext();

        usernameText = findViewById(R.id.input_username);
        passwordText = findViewById(R.id.input_password);
        loginButton = findViewById(R.id.btn_login);

        progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            Log.e("receiver", "Got message: " + message);

            // Check message received by LoginService, change UI
        }
    };

    public void login() {
        Log.d(TAG, "Login");

        /*if (!validate()) {
            onLoginFailed();
            return;
        }*/

        // check internet is available here


        // ok, input is valid and internet is available
        loginButton.setEnabled(false);

        progressDialog.show();

        Intent loginIntent = new Intent(this, LoginService.class);

        loginIntent.putExtra("username", username);
        loginIntent.putExtra("password", password);  // non in chiaro possibilmente
        loginIntent.putExtra("application", application);
        loginIntent.putExtra("tenant", tenant);
        loginIntent.putExtra("grant_type", grant_type);

        startService(loginIntent);

        /*String email = usernameText.getText().toString();
        String password = passwordText.getText().toString();*/

        // TODO: Implement your own authentication logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                       Log.e("Progress Dialog Thread", "Dismissing");
                        progressDialog.dismiss();
                        startActivity(new Intent(context, MainActivity.class));
                        finish();
                    }
                }, 3000);
    }

    @Override
    protected void onResume(){
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-event-name"));
    }

    @Override
    protected void onPause (){
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }



    private void onLoginSuccess() {
        loginButton.setEnabled(true);
        finish();
    }

    private void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        loginButton.setEnabled(true);
    }

    private boolean validate() {
        boolean valid = true;

        if (username.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
            usernameText.setError("enter a valid email address");
            valid = false;
        } else {
            usernameText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        return valid;
    }
}
