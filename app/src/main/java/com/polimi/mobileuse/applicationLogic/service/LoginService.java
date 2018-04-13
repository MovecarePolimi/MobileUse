package com.polimi.mobileuse.applicationLogic.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.polimi.mobileuse.logic.http.HttpLogin;
import com.polimi.mobileuse.logic.http.TokenManager;

public class LoginService extends IntentService {
    private static final String TAG = "LoginService";

    private final static String BROADCAST_EVENT_NAME    = "login_event";

    private static final String USERNAME_EXTRA          = "username";
    private static final String PASSWORD_EXTRA          = "password";

    private Context context;

    public LoginService() {
        super("LoginService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.v("LoginService","Start");

        this.context = getApplicationContext();

        TokenManager tokenManager = new TokenManager(context, BROADCAST_EVENT_NAME);
        HttpLogin httpLogin = new HttpLogin(context, BROADCAST_EVENT_NAME);

        final String USERNAME = intent.getStringExtra(USERNAME_EXTRA);
        final String PASSWORD = intent.getStringExtra(PASSWORD_EXTRA);

        Log.v("LoginService", USERNAME +" - "+PASSWORD);



        boolean tokenGet = tokenManager.retrieveToken(USERNAME, PASSWORD);
        if(tokenGet) {
            httpLogin.doLogin();
        } else{
            Log.e(TAG, "Cannot get the token");
        }

    }


}
