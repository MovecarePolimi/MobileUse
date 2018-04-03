package com.polimi.movecare_r01.applicationLogic.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.polimi.movecare_r01.logic.http.HttpLoginManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoginService extends IntentService {

    private Context context;

    public LoginService() {
        super("LoginService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e("LoginService","Start");

        this.context = getApplicationContext();

        final String GRANT_TYPE = intent.getStringExtra("grant_type");
        final String USERNAME = intent.getStringExtra("username");
        final String PASSWORD = intent.getStringExtra("password");
        final String APPLICATION = intent.getStringExtra("application");
        final String TENANT = intent.getStringExtra("tenant");


        Log.e("LoginService", USERNAME +" - "+PASSWORD);

        HttpLoginManager httpLoginManager = new HttpLoginManager(context);
        final String[] params = {
                GRANT_TYPE,
                USERNAME,
                PASSWORD,
                APPLICATION,
                TENANT
        };

        try {
            httpLoginManager.login(params);
        } catch (IOException e) {
            e.printStackTrace();
        }

        sendMessage();
        // Check internet connection:
        // https://developer.android.com/training/monitoring-device-state/connectivity-monitoring.html
    }

    private void sendMessage() {
        Log.e("sender", "Broadcasting message");
        Intent intent = new Intent("custom-event-name");

        intent.putExtra("message", "This is my message!");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
