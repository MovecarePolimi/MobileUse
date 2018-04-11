package com.polimi.movecare_r01.applicationLogic.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.polimi.movecare_r01.logic.http.HttpLoginManager;

public class LoginService extends IntentService {
    private static final String TAG = "LoginService";

    private static final String DO_REFRESH_TOKEN_EXTRA  = "do_refresh_token";
    private static final String REFRESH_TOKEN_EXTRA     = "refresh_token";

    private static final String GRANT_TYPE_EXTRA        = "grant_type";
    private static final String USERNAME_EXTRA          = "username";
    private static final String PASSWORD_EXTRA          = "password";
    private static final String APPLICATION_EXTRA       = "application";
    private static final String TENANT_EXTRA            = "tenant";

    private Context context;

    public LoginService() {
        super("LoginService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.v("LoginService","Start");

        this.context = getApplicationContext();

        HttpLoginManager httpLoginManager = new HttpLoginManager(context);

        final boolean doRefreshToken = intent.getBooleanExtra(DO_REFRESH_TOKEN_EXTRA, false);
        final String GRANT_TYPE = intent.getStringExtra(GRANT_TYPE_EXTRA);
        final String APPLICATION = intent.getStringExtra(APPLICATION_EXTRA);
        final String TENANT = intent.getStringExtra(TENANT_EXTRA);

        if(doRefreshToken){
            Log.v(TAG, "Do Refresh Token");
            final String REFRESH_TOKEN = intent.getStringExtra(REFRESH_TOKEN_EXTRA);
            final String[] params = {
                    GRANT_TYPE,
                    REFRESH_TOKEN,
                    APPLICATION,
                    TENANT
            };
            httpLoginManager.doRefreshToken(params);
        } else{
            final String USERNAME = intent.getStringExtra(USERNAME_EXTRA);
            final String PASSWORD = intent.getStringExtra(PASSWORD_EXTRA);

            Log.v("LoginService", USERNAME +" - "+PASSWORD);

            final String[] params = {
                    GRANT_TYPE,
                    USERNAME,
                    PASSWORD,
                    APPLICATION,
                    TENANT
            };

            httpLoginManager.doLogin(params);
        }
    }


}
