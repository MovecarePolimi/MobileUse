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
        setContentView(R.layout.activity_splash);
        this.context = getApplicationContext();

        Thread welcomeThread = new Thread() {
            @Override
            public void run() {
                try {
                    super.run();
                    sleep(3000);  //Delay of 3 seconds

                    LoginPreferences log = new LoginPreferences();
                    String token = log.getVariable(context, LoginPreferences.Variable.ACCESS_TOKEN);
                    String username = log.getVariable(context, LoginPreferences.Variable.USERNAME);
                    String uuid = log.getVariable(context, LoginPreferences.Variable.UUID);
                    //String email = log.getVariable(context, LoginPreferences.Variable.EMAIL);

                    if(!isValidString(token)){
                        Log.e(TAG, "Login is required");

                        startActivity(new Intent(context, LoginActivity.class));
                        finish();
                    } else{
                        // controllare cosa succede se un utente non ha email
                        if(!checkTokenValidity(token) || !checkDataValidity(username, uuid)){
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
        };
        welcomeThread.start();
    }


    // Check any token pattern here: number of chars, expiration date, etc
    private boolean checkTokenValidity(String token){
        // check expiration date??
        return true;
    }

    // Email not checked since a user may not have an email address
    private boolean checkDataValidity(String username, String uuid){
        return !(username == null || uuid == null ||
                username.isEmpty() || uuid.isEmpty());
    }

    private boolean isValidString(String val){
        return !(val == null || val.isEmpty());
    }
    private void refreshToken(){

    }
}
