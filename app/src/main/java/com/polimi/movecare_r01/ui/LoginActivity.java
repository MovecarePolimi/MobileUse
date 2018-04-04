package com.polimi.movecare_r01.ui;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.polimi.movecare_r01.R;
import com.polimi.movecare_r01.applicationLogic.service.LoginService;
import com.polimi.movecare_r01.logic.http.HttpLoginManager;
import com.polimi.movecare_r01.ui.fragment.LoginErrorFragment;
import com.polimi.movecare_r01.ui.fragment.LoginNoInternetFragment;
import com.polimi.movecare_r01.ui.interfaces.LoginDialogListener;


public class LoginActivity extends AppCompatActivity implements LoginDialogListener{
    private static final String TAG = "LoginActivity";

    private static final String BROADCAST_LOGIN_EVENT = "";

    private Context context;

    private EditText usernameText;
    private EditText passwordText;
    private Button loginButton;
    private ProgressDialog progressDialog;

    // Variables for testing
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
    }

    public void loginClicked(View view) {
        Log.d(TAG, "Login");

        /*String email = usernameText.getText().toString();
        String password = passwordText.getText().toString();*/

        String username = "user101";
        String password = "123456";

        /*if (!validate(email, password)) {
            showLoginErrorDialog();
            return;
        }*/

        forceCloseKeyboard();

        loginButton.setEnabled(false);
        progressDialog.show();

        Intent loginIntent = new Intent(this, LoginService.class);

        loginIntent.putExtra("username", username);
        loginIntent.putExtra("password", password);  // non in chiaro possibilmente
        loginIntent.putExtra("application", application);
        loginIntent.putExtra("tenant", tenant);
        loginIntent.putExtra("grant_type", grant_type);

        startService(loginIntent);



    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            HttpLoginManager.MessageType m = (HttpLoginManager.MessageType)intent.getSerializableExtra("message");
            Log.e("receiver", "Got message: " + m);

            if(m == HttpLoginManager.MessageType.MGS_NO_INTERNET){
                Log.e(TAG,"Login ERROR: no internet");
                showLoginNoInternetDialog();
            } else if(m == HttpLoginManager.MessageType.MSG_ERR){
                Log.e(TAG,"Login ERROR: credential error");
                showLoginErrorDialog();

            } else if(m == HttpLoginManager.MessageType.MSG_OK){
                Log.e(TAG,"Login OK");

                startActivity(new Intent(context, MainActivity.class));
                finish();
            } else{
                Log.e(TAG, "Unexpeced behaviour: received unknown value "+m);
            }

            progressDialog.dismiss();
        }
    };


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



    private void setLoginButtonState(boolean enable) {
        loginButton.setEnabled(enable);
    }


    private boolean validate(String username, String password) {
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

    private void forceCloseKeyboard(){
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            if(imm != null){
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    private void showLoginErrorDialog(){
        DialogFragment loginErrorFragment = new LoginErrorFragment();
        loginErrorFragment.setCancelable(false);
        loginErrorFragment.show(getSupportFragmentManager(), "permission");
    }

    private void showLoginNoInternetDialog(){
        DialogFragment loginNoInternetFragment = new LoginNoInternetFragment();
        loginNoInternetFragment.setCancelable(false);
        loginNoInternetFragment.show(getSupportFragmentManager(), "permission");
    }
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        Log.e(TAG, "Dialog Positive Click");
        loginButton.setEnabled(true);
    }
}
