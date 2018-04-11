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
import com.polimi.movecare_r01.dao.preferences.LoginPreferences;
import com.polimi.movecare_r01.logic.http.HttpLoginManager;
import com.polimi.movecare_r01.ui.fragment.LoginErrorFragment;
import com.polimi.movecare_r01.ui.interfaces.LoginDialogListener;


public class LoginActivity extends AppCompatActivity implements LoginDialogListener{
    private static final String TAG = "LoginActivity";

    private static final String DO_REFRESH_TOKEN_EXTRA  = "do_refresh_token";
    private static final String REFRESH_TOKEN_EXTRA     = "refresh_token";

    private static final String GRANT_TYPE_EXTRA        = "grant_type";
    private static final String USERNAME_EXTRA          = "username";
    private static final String PASSWORD_EXTRA          = "password";
    private static final String APPLICATION_EXTRA       = "application";
    private static final String TENANT_EXTRA            = "tenant";

    private Context context;

    private EditText usernameText;
    private EditText passwordText;
    private Button loginButton;
    private ProgressDialog progressDialog;

    private static final String grant_type = "password";
    // probably these two should be changed by EURECAT
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

        String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();

        if (!validate(username, password)) {
            return;
        }

        forceCloseKeyboard();

        loginButton.setEnabled(false);
        progressDialog.show();

        Intent loginIntent = new Intent(this, LoginService.class);

        loginIntent.putExtra(USERNAME_EXTRA, username);
        loginIntent.putExtra(PASSWORD_EXTRA, password);  // non in chiaro possibilmente
        loginIntent.putExtra(APPLICATION_EXTRA, application);
        loginIntent.putExtra(TENANT_EXTRA, tenant);
        loginIntent.putExtra(GRANT_TYPE_EXTRA, grant_type);

        startService(loginIntent);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            HttpLoginManager.MessageType m = (HttpLoginManager.MessageType)intent.getSerializableExtra("message");
            Log.i("receiver", "Got message: " + m);

            switch (m){
                case MGS_NO_INTERNET: {
                    Log.i(TAG,"Login ERROR: no internet");
                    showLoginErrorDialog(
                            context.getString(R.string.loginError_title),
                            context.getString(R.string.loginError_noInternet));
                    break;
                }
                case MSG_BAD_CREDENTIALS: {
                    Log.i(TAG,"Login ERROR: credential error");
                    showLoginErrorDialog(
                            context.getString(R.string.loginError_title),
                            context.getString(R.string.loginError_wrongCredentials)
                    );
                    break;
                }
                case MSG_INVALID_TOKEN: {
                    Log.i(TAG,"Login ERROR: invalid token");

                    LoginPreferences logsPreferences = new LoginPreferences();

                    Intent refreshTokenIntent = new Intent(context, LoginService.class);
                    refreshTokenIntent.putExtra(DO_REFRESH_TOKEN_EXTRA, true); // boolean

                    refreshTokenIntent.putExtra(REFRESH_TOKEN_EXTRA,
                            logsPreferences.getVariable(context, LoginPreferences.Variable.REFRESH_TOKEN));
                    refreshTokenIntent.putExtra(APPLICATION_EXTRA, application);
                    refreshTokenIntent.putExtra(TENANT_EXTRA, tenant);
                    refreshTokenIntent.putExtra(GRANT_TYPE_EXTRA, grant_type);

                    startService(refreshTokenIntent);
                    break;
                }
                case MSG_INVALID_REFRESH_TOKEN:{
                    // Login again
                    Log.i(TAG,"Login ERROR: invalid refresh token");
                    showLoginErrorDialog(
                            context.getString(R.string.loginError_title),
                            context.getString(R.string.loginError_newLoginRequired)
                    );
                }
                case MSG_ERR: {
                    Log.i(TAG,"Login ERROR: server error");
                    showLoginErrorDialog(
                            context.getString(R.string.loginError_title),
                            context.getString(R.string.loginError_fatalError)
                    );
                    break;
                }
                case MSG_OK: {
                    Log.i(TAG,"Login OK");

                    startActivity(new Intent(context, MainActivity.class));
                    finish();
                    break;
                }
                default: {
                    Log.i(TAG, "Unexpeced behaviour: received unknown value "+m);
                    showLoginErrorDialog(
                            context.getString(R.string.loginError_title),
                            context.getString(R.string.loginError_fatalError)
                    );
                }
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


    private boolean validate(String username, String password) {
        boolean valid = true;

        if (username.isEmpty()) {
            usernameText.setError("enter a valid username");
            valid = false;
        } else {
            usernameText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4) {
            passwordText.setError("at least 4 alphanumeric characters");
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

    private void showLoginErrorDialog(String title, String text){
        DialogFragment loginErrorFragment = new LoginErrorFragment();
        loginErrorFragment.setCancelable(false);

        Bundle data = new Bundle();
        data.putString("title", title);
        data.putString("text", text);
        loginErrorFragment.setArguments(data);

        loginErrorFragment.show(getSupportFragmentManager(), "permission");
    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        loginButton.setEnabled(true);
    }
}
