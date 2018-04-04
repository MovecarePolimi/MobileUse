package com.polimi.movecare_r01.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.polimi.movecare_r01.R;
import com.polimi.movecare_r01.dao.preferences.LoginPreferences;

public class SplashActivity extends AppCompatActivity {

    private final static String TAG = "SplashActivity";
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getApplicationContext();

        /* If we need to implement Splash Screen with a sleeping thread: not suggested

        setContentView(R.layout.activity_splash);
        this.context = getApplicationContext();
        Thread welcomeThread = new Thread() {
            @Override
            public void run() {
                initialize();
            }
        };
        welcomeThread.start();

        */

        initialize();

    }

    private void initialize(){
        try {
            LoginPreferences log = new LoginPreferences();
            String access_token = log.getVariable(context, LoginPreferences.Variable.ACCESS_TOKEN);
            String refresh_token = log.getVariable(context, LoginPreferences.Variable.REFRESH_TOKEN);
            String username = log.getVariable(context, LoginPreferences.Variable.USERNAME);
            String uuid = log.getVariable(context, LoginPreferences.Variable.UUID);
            String email = log.getVariable(context, LoginPreferences.Variable.EMAIL);

            if(!isValidString(access_token)){
                Log.e(TAG, "Login is required");

                startActivity(new Intent(context, LoginActivity.class));
                finish();
            } else{
                if(!checkTokenValidity(access_token) || !checkDataValidity(username, uuid, email)){
                    Log.e(TAG, "Token is not syntactically valid, refresh");
                    // refresh token, then store new token and user data
                    refreshToken();
                }

                // at this point, every data is valid
                startActivity(new Intent(context, MainActivity.class));
                finish();
            }

        } catch (Exception e) {
            Log.e(TAG, "Exception thrown");
            e.printStackTrace();
            finish();
        }
    }

    private boolean checkTokenValidity(String token){
        // Check any token pattern here: number of chars, expiration date, etc
        return true;
    }

    // Email not checked since a user may not have an email address
    private boolean checkDataValidity(String username, String uuid, String email){
        return !(username == null || uuid == null || email == null ||
                username.isEmpty() || uuid.isEmpty() || email.isEmpty());
    }

    private boolean isValidString(String val){
        return !(val == null || val.isEmpty());
    }

    private void refreshToken(){

    }
}
