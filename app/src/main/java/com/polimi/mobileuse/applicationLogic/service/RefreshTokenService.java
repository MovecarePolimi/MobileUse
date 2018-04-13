package com.polimi.mobileuse.applicationLogic.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.polimi.mobileuse.logic.http.TokenManager;

public class RefreshTokenService extends IntentService {
    private static final String TAG = "RefreshTokenService";

    // this service is only executed to refresh token when store request failed
    private final static String BROADCAST_EVENT_NAME    = "store_event";

    private Context context;

    public RefreshTokenService() {
        super("LoginService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.v("RefreshTokenService","Start");

        this.context = getApplicationContext();

        TokenManager tokenManager = new TokenManager(context, BROADCAST_EVENT_NAME);
        tokenManager.refreshToken();


    }


}
