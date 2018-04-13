package com.polimi.mobileuse.applicationLogic.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.polimi.mobileuse.dao.preferences.SharedPreferencesManager;
import com.polimi.mobileuse.logic.alarm.MyAlarmManager;

public class InitializationService extends IntentService {
    private static final String TAG = InitializationService.class.getSimpleName();

    private static final String FIRST_ATTEMPT = "firstAttempt";

    public InitializationService() {
        super("InitializationService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "Start service");


        // **** 1 **** -  Set shared preferences
        setSharedPreferences();

        // **** 2 **** -  Set alarms: report and battery checking (11.00 - 16.00)
        setAlarms();

        /* For testing purpose */
        //startService(new Intent(this, ReadLogsService.class));

        Log.i(TAG, "End service");
    }

    private void setAlarms(){
        MyAlarmManager myAlarmManager = new MyAlarmManager(this);
        myAlarmManager.setAppAlarms();
    }

    private void setSharedPreferences(){
        SharedPreferencesManager sharedPreferencesMng = new SharedPreferencesManager();
        sharedPreferencesMng.initializeSettings(this);
    }


}
