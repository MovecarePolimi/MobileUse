package com.polimi.movecare_r01.applicationLogic.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.polimi.movecare_r01.applicationLogic.schedule.MyJobScheduler;
import com.polimi.movecare_r01.dao.preferences.SharedPreferencesManager;
import com.polimi.movecare_r01.logic.exceptions.InternetConnectionException;
import com.polimi.movecare_r01.logic.http.ConnectionManager;
import com.polimi.movecare_r01.logic.notification.NoInternetNotification;
import com.polimi.movecare_r01.logic.notification.ServerErrorNotification;

import java.io.IOException;
import java.util.Calendar;

public class SendReportService extends IntentService {
    private static final String TAG = SendReportService.class.getSimpleName();

    private static final String MILLIS_DATE = "millis_date";

    public SendReportService() {
        super("SendReportService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "Service SendReportService: start");
        String jsonStringSUM = intent.getStringExtra("SUM");
        String jsonStringSUC = intent.getStringExtra("SUC");
        String jsonStringSSU = intent.getStringExtra("SSU");
        String jsonStringPSU = intent.getStringExtra("PSU");

        //String timeMillis = intent.getStringExtra(MILLIS_DATE);

        Log.e("*** SUM ***", jsonStringSUM);
        Log.e("*** SUC ***", jsonStringSUC);
        Log.e("*** SSU ***", jsonStringSSU);
        Log.e("*** PSU ***", jsonStringPSU);

        ConnectionManager cm = new ConnectionManager(this);

        try {
            cm.sendJSON(jsonStringSUM, jsonStringSUC, jsonStringSSU, jsonStringPSU, null);
        } catch (InternetConnectionException e) {
            Log.e(TAG, "No internet connection available");

            //send notification
            NoInternetNotification noInternetNotification = new NoInternetNotification(this);
            noInternetNotification.setOpenWifiSettings();
            noInternetNotification.setAutoCancel(true);
            noInternetNotification.send();

            //setReportScheduling(true, timeMillis);
            setReportScheduling(true);

        } catch(IOException e) {
            Log.e(TAG, "Exception: server error" );

            ServerErrorNotification serverErrorNotification = new ServerErrorNotification(this);
            serverErrorNotification.setOpenAppSettings();
            serverErrorNotification.setAutoCancel(true);
            serverErrorNotification.send();

            //setReportScheduling(false, timeMillis);
            setReportScheduling(false);
        }

        Log.i(TAG, "Service SendReportService: end");
    }

    /** Report attempts are scheduled during the night, from 22:00 to 20:00 (next day)
     *
     * @return true if the report must be scheduled again, false otherwise
     */
    private boolean reportToBeScheduled(){
        SharedPreferencesManager sharedPreferencesMgr = new SharedPreferencesManager();
        int morningHour = sharedPreferencesMgr.getBatteryCheckStart();
        int reportHour = sharedPreferencesMgr.getReportHour();

        int nowHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        boolean res = nowHour >= reportHour || (nowHour >= 0 && nowHour <= morningHour);
        return res;

    }

    private void setReportScheduling(boolean onlyInternetIssue){

        // if report not sent (scheduling needed), from 22:00 to 08:00 the time is stored into
        // a separate shared preferences variable.

        SharedPreferencesManager sharedPreferencesMgr = new SharedPreferencesManager();
        String timeMillis  = sharedPreferencesMgr.getMillisDate(this);

        if (timeMillis == null){
            timeMillis = String.valueOf(Calendar.getInstance().getTimeInMillis());
            sharedPreferencesMgr.setMillisDate(this, timeMillis);
        }

        /*if(timeMillis != null){
            SharedPreferencesManager sharedPreferencesMgr = new SharedPreferencesManager();
            sharedPreferencesMgr.setMillisDate(this, timeMillis);
        } else{
            SharedPreferencesManager sharedPreferencesMgr = new SharedPreferencesManager();
            timeMillis = sharedPreferencesMgr.getMillisDate(this);
        }*/

        // We are not in the right time slot, stop trying send report, delete tmp variable
        // and add it to the missing list.
        if(!reportToBeScheduled()){
            // Store times into list
            //SharedPreferencesManager sharedPreferencesMgr = new SharedPreferencesManager();
            sharedPreferencesMgr.addMissingDateElement(this, timeMillis);
            sharedPreferencesMgr.removeMillisDate(this);
            return;
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.i(TAG, "Current API > 21");

            MyJobScheduler myJobScheduler = new MyJobScheduler();
            myJobScheduler.scheduleSendReportJob(
                        this,
                        onlyInternetIssue);
            Log.i(TAG, "End Scheduling");

        } else {
            Log.e("*****", "Job Scheduler Not Supported: minimum API is 21");
        }
    }


    /*private void storeReportTime (long timeMillis){
        SharedPreferencesManager sharedPreferencesMgr = new SharedPreferencesManager();
        sharedPreferencesMgr.addMissingDateElement(this, String.valueOf(timeMillis));
    }*/

}
