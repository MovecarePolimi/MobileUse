package com.polimi.mobileuse.logic.alarm;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.polimi.mobileuse.applicationLogic.receiver.BatteryCheckerReceiver;
import com.polimi.mobileuse.applicationLogic.receiver.ReportAlarmReceiver;
import com.polimi.mobileuse.dao.preferences.SharedPreferencesManager;

import java.util.Calendar;

public class MyAlarmManager {
    private static final String TAG = MyAlarmManager.class.getSimpleName();

    private final static int REPORT_ALARM_ID            = 888;
    private final static int MORNING_ALARM_ID           = 777;
    private final static String ALARM_ID_STRING         = "alarmID";

    private Context context;
    private AlarmManager alarmManager;

    public MyAlarmManager(Context context){
        this.context = context;
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public void setAppAlarms(){
        setReportAlarm();
        setMorningAlarm();
    }

    private void setReportAlarm(){
        Intent reportIntent = new Intent(context, ReportAlarmReceiver.class);

        // to cancel the battery alarm when report alarm is triggered (at 22.00)
        PendingIntent reportPendingIntent = PendingIntent.getBroadcast(
                context, REPORT_ALARM_ID, reportIntent, 0);

        SharedPreferencesManager sharedPreferencesMgr = new SharedPreferencesManager();
        int reportHour = sharedPreferencesMgr.getReportHour();

        // Set Report alarm everyday at 10:00 PM
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, reportHour);


        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                reportPendingIntent
        );
    }

    private void setMorningAlarm(){
        Intent morningIntent = new Intent(context, BatteryCheckerReceiver.class);

        SharedPreferencesManager sharedPreferencesMgr = new SharedPreferencesManager();
        int reportHour = sharedPreferencesMgr.getReportHour(); // Report alarm set at 22
        int batteryCheckStartHours = sharedPreferencesMgr.getBatteryCheckStart();
        int batteryCheckFrequency = sharedPreferencesMgr.getBatteryFrequency();

        Calendar now = Calendar.getInstance();
        int currentHour = now.get(Calendar.HOUR_OF_DAY);
        int startingBatteryHour;

        // Check if current hour is in the range 8 - 21
        if(currentHour >= batteryCheckStartHours && currentHour < reportHour){
            startingBatteryHour = currentHour;
        } else{
            startingBatteryHour = batteryCheckStartHours;
        }
        morningIntent.putExtra(ALARM_ID_STRING, MORNING_ALARM_ID);
        PendingIntent morningPendingIntent = PendingIntent.getBroadcast(
                context, MORNING_ALARM_ID, morningIntent, 0);

        // Set Report alarm everyday at 10:00 PM
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, startingBatteryHour);

        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                batteryCheckFrequency*AlarmManager.INTERVAL_HOUR,  // every 1 hours
                morningPendingIntent
        );
    }


}
