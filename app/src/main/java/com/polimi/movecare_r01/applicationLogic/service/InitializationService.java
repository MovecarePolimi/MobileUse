package com.polimi.movecare_r01.applicationLogic.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.polimi.movecare_r01.dao.preferences.SharedPreferencesManager;
import com.polimi.movecare_r01.logic.alarm.MyAlarmManager;
import com.polimi.movecare_r01.logic.insoles.InsolesManager;
import com.polimi.movecare_r01.logic.notification.NoInternetNotification;
import com.polimi.movecare_r01.model.insoles.Insoles;
import com.polimi.movecare_r01.model.insoles.InsolesHeader;

import java.util.Calendar;
import java.util.List;

public class InitializationService extends IntentService {
    private static final String TAG = InitializationService.class.getSimpleName();

    private static final String SIM_SERIAL_NUMBER = "simSerialNumber";
    private static final String FIRST_ATTEMPT = "firstAttempt";

    public InitializationService() {
        super("InitializationService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "Start service");

        //String simSerialNumber = intent.getStringExtra(SIM_SERIAL_NUMBER);

        // **** 2 **** -  Set alarms: report and battery checking (11.00 - 16.00)
        //setSharedPreferences();

        // **** 2 **** -  Set alarms: report and battery checking (11.00 - 16.00)
        //setAlarms();

        // **** 3 **** -  Store user data into key-value file (sharedpreferences)
        //storeUserData(simSerialNumber);

        // **** REPORT - Non deve essere chiamato qui ma triggerato dal ReportAlarmReceiver
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

    /*private void storeUserData(String simSerialNumber){
        Intent userDataIntent = new Intent(this, StoreUserDataService.class);
        User u = new User();
        u.setName("Pasquale");
        u.setSurname("Franchino");
        u.setPhoneNumber(simSerialNumber);
        userDataIntent.putExtra("utente", u);
        startService(userDataIntent);
    }*/

    /*private User userLogin(){
        Intent loginIntent = new Intent(this, LoginService.class);
        loginIntent.setData(Uri.parse(simSerialNumber));
        startService(loginIntent);

        return new User();
    }*/

}
