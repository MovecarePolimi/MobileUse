package com.polimi.movecare_r01.applicationLogic.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.polimi.movecare_r01.R;
import com.polimi.movecare_r01.dao.preferences.SharedPreferencesManager;
import com.polimi.movecare_r01.logic.battery.BatteryChecker;
import com.polimi.movecare_r01.logic.notification.BatteryThresholdNotification;
import com.polimi.movecare_r01.ui.MainActivity;

import java.util.Calendar;

public class BatteryCheckerReceiver extends BroadcastReceiver {
    private static final String TAG = BatteryCheckerReceiver.class.getSimpleName();

    private final static String ALARM_ID_STRING         = "alarmID";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(this.getClass().getSimpleName(), "Start Receiver");

        // Set ReportSent to false
        SharedPreferencesManager sharedPreferencesMng = new SharedPreferencesManager();
        if(sharedPreferencesMng.getReportSent(context)){
            sharedPreferencesMng.setReportSent(context, false);
        }

        SharedPreferencesManager sharedPreferencesMgr = new SharedPreferencesManager();
        int batteryCheckEnd = sharedPreferencesMgr.getBatteryCheckEnd();
        //int batteryLowLevel = sharedPreferencesMgr.getBatteryLow();

        // Check if the alarm should be stopped
        Calendar now = Calendar.getInstance();

        if(now.get(Calendar.HOUR_OF_DAY) > batteryCheckEnd){
            Log.e(TAG,"Cancel Daily Alarm");
            int alarmId = intent.getExtras().getInt(ALARM_ID_STRING);

            PendingIntent alarmIntent = PendingIntent.getBroadcast(
                    context, alarmId,
                    new Intent(context, BatteryCheckerReceiver.class),
                    PendingIntent.FLAG_CANCEL_CURRENT);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            alarmManager.cancel(alarmIntent);
            return;
        }

        BatteryChecker batteryChecker = null;
        try {
            batteryChecker = new BatteryChecker(context);
        } catch (Exception e) {
            Log.e(TAG, "Exception caught: cannot read battery level or charging state");
            e.printStackTrace();
            return;
        }

        if(batteryChecker.isCharging()){
            Log.i(TAG, "Device is charging");
            return;
        }

        /*// if battery level is <20%, return
        if(batteryLevel <= batteryLowLevel){
            Log.e(TAG, "Battery is low, stop checking level and sending notifications");
            setMainActivityStrings(context.getString(R.string.recharge_message), true);

            return;
        }*/

        if (batteryChecker.isUnderThreshold()){
            Log.v(TAG, "Battery under 40% - show notification");

            setMainActivityStrings(context.getString(R.string.recharge_message), true);

            BatteryThresholdNotification thresholdNotification = new BatteryThresholdNotification(context);
            thresholdNotification.setOpenAppSettings();
            thresholdNotification.send();
        } else{
            Log.v(TAG, "Battery over 40% - set text");

            setMainActivityStrings(context.getString(R.string.app_running), false);
        }

        Log.i(this.getClass().getSimpleName(), "End Receiver");
    }

    private void setMainActivityStrings(String text, boolean isWarning){
        try {
            if (MainActivity.getInst() != null)
                MainActivity.getInst().updateUIStrings(text, isWarning);
        } catch (Exception e) {
            Log.e(TAG, "Exception caught: cannot update UI strings");
            e.printStackTrace();
        }
    }

}
