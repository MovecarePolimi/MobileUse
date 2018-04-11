package com.polimi.mobileuse.applicationLogic.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.polimi.mobileuse.dao.preferences.SharedPreferencesManager;
import com.polimi.mobileuse.logic.exceptions.InternetConnectionException;
import com.polimi.mobileuse.logic.http.ConnectionManager;

import java.io.IOException;

public class SendMissingReportService extends IntentService {
    private static final String TAG = SendMissingReportService.class.getSimpleName();

    public SendMissingReportService() {
        super("SendMissingReportService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "Service SendMissingReportService: start");
        String jsonStringSUM = intent.getStringExtra("SUM");
        String jsonStringSUC = intent.getStringExtra("SUC");
        String jsonStringSSU = intent.getStringExtra("SSU");
        String jsonStringPSU = intent.getStringExtra("PSU");

        SharedPreferencesManager sharedPreferencesMgr = new SharedPreferencesManager();
        String timeMillis = intent.getStringExtra(sharedPreferencesMgr.getMillisDate(this));

        Log.e("*** OLD SUM ***", jsonStringSUM);
        Log.e("*** OLD SUC ***", jsonStringSUC);
        Log.e("*** OLD SSU ***", jsonStringSSU);
        Log.e("*** OLD PSU ***", jsonStringPSU);

        ConnectionManager cm = new ConnectionManager(this);
        //cm.provaOk(timeMillis);


        try {
            cm.sendJSON(jsonStringSUM, jsonStringSUC, jsonStringSSU, jsonStringPSU, timeMillis);
        } catch (InternetConnectionException e) {
            Log.e(TAG, "No internet connection available");
        } catch(IOException e) {
            Log.e(TAG, "Exception: server error" );
        }

        Log.i(TAG, "Service SendMissingReportService: end");
    }

}
