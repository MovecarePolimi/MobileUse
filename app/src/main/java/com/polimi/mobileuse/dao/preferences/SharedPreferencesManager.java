package com.polimi.mobileuse.dao.preferences;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SharedPreferencesManager {

    private static final String TAG = SharedPreferencesManager.class.getSimpleName();

    /*
    *   REPORT SETTINGS
    *   report_hour = 22
    *   last_report_hour = 20
    *   report_interval = 1
    *   report_sent = T/F
    *
    * */
    private static final String REPORT_SETTINGS_FILE        = "ReportSettings";

    private static final String REPORT_SENT_STRING          = "report_sent";

    private static final String REPORT_HOUR_STRING          = "report_hour";
    private static final int    REPORT_HOUR                 = 22;

    private static final String REPORT_INTERVAL_STRING      = "report_interval";
    private static final int    REPORT_INTERVAL             = 1;

    private static final String LAST_REPORT_HOUR_STRING     = "last_report_hour";
    private static final int    LAST_REPORT_HOUR            = REPORT_HOUR-2;


    /*
    *   BATTERY SETTINGS
    *   battery_check_start = 8
    *   battery_check_end = 20
    *   battery_frequency = 1
    *   battery_threshold = 40
    *
    *
    * */
    private static final String BATTERY_SETTINGS_FILE       = "BatterySettings";

    private static final String BATTERY_CHECK_START_STRING  = "battery_check_start";
    private static final int    BATTERY_CHECK_START         = 8;

    private static final String BATTERY_CHECK_END_STRING    = "battery_check_end";
    private static final int    BATTERY_CHECK_END           = 20;

    private static final String BATTERY_FREQUENCY_STRING    = "battery_frequency";
    private static final int    BATTERY_FREQUENCY           = 1;

    private static final String BATTERY_THRESHOLD_STRING    = "battery_threshold";
    private static final int    BATTERY_THRESHOLD           = 40;

    private static final String BATTERY_LOW_STRING          = "battery_low";
    private static final int    BATTERY_LOW                 = 20;


    /*
    *   MISSING REPORT
    *   date = [123, 12344, 12344]
    *   millis_date = 1234555
    *
    *
    * */
    private static final String MISSING_REPORT_FILE         = "MissingReport";

    private static final String MISSING_DATE_STRING         = "date";

    private static final String MILLIS_DATE_STRING          = "millis_date";

    public void initializeSettings(Context context){
        initializeReportSettings(context);
        initializeBatterySettings(context);
    }

    private void initializeReportSettings(Context context){
        SharedPreferences settings = context.getSharedPreferences(REPORT_SETTINGS_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();

        editor.putInt(REPORT_HOUR_STRING, REPORT_HOUR);
        editor.putInt(LAST_REPORT_HOUR_STRING, LAST_REPORT_HOUR);
        editor.putInt(REPORT_INTERVAL_STRING, REPORT_INTERVAL);

        editor.apply();

        Log.i(TAG, REPORT_SETTINGS_FILE+" initialized");
    }

    private void initializeBatterySettings(Context context){
        SharedPreferences settings = context.getSharedPreferences(BATTERY_SETTINGS_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();

        editor.putInt(BATTERY_CHECK_START_STRING, BATTERY_CHECK_START);
        editor.putInt(BATTERY_CHECK_END_STRING, BATTERY_CHECK_END);
        editor.putInt(BATTERY_FREQUENCY_STRING, BATTERY_FREQUENCY);
        editor.putInt(BATTERY_THRESHOLD_STRING, BATTERY_THRESHOLD);
        editor.putInt(BATTERY_LOW_STRING, BATTERY_LOW);

        editor.apply();

        Log.i(TAG, BATTERY_SETTINGS_FILE+" initialized");
    }

    public void setReportSent(Context context, boolean value) {
        SharedPreferences settings = context.getSharedPreferences(REPORT_SETTINGS_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();

        editor.putBoolean(REPORT_SENT_STRING, value);
        editor.apply();

        Log.i(TAG, "Variable " + REPORT_SENT_STRING + " set " + value);
    }

    public void setMissingDateList(Context context, List<String> missingReport){
        if(context == null || missingReport == null){
            Log.e(TAG, "SetMissingReport: received null or empty objects");
            return;
        }
        SharedPreferences settings = context.getSharedPreferences(MISSING_REPORT_FILE, 0);
        SharedPreferences.Editor edit = settings.edit();

        Set<String> set = new HashSet<>();

        set.addAll(missingReport);
        edit.putStringSet(MISSING_DATE_STRING, set);

        edit.apply();
    }

    public void addMissingDateElement(Context context, String millisDate){

        if(context == null || millisDate == null){
            Log.e(TAG, "AddMissingReportElement: received null objects");
            return;
        }

        List<String> storedList = getMissingDateList(context);
        if(storedList == null){
            storedList = new ArrayList<>();
        }

        storedList.add(millisDate);
        setMissingDateList(context, storedList);
        Log.i(TAG, "AddMissingReportElement: new element added");
    }

    public void removeMissingDateElement(Context context, String millisDate){
        if(context == null || millisDate == null){
            Log.e(TAG, "removeMissingReportElement: received null objects");
            return;
        }
        List<String> storedList = getMissingDateList(context);
        if(storedList == null || storedList.isEmpty()){
            Log.e(TAG, "removeMissingReportElement: current list is null or empty");
            return;
        }

        storedList.remove(millisDate);
        setMissingDateList(context, storedList);

        Log.i(TAG, "RemoveMissingReportElement: element removed");
    }

    public List<String> getMissingDateList(Context context){
        SharedPreferences settings = context.getSharedPreferences(MISSING_REPORT_FILE, 0);

        Set<String> set = settings.getStringSet(MISSING_DATE_STRING, null);

        if(set == null){
            return null;
        }
        //List<String> missingReportList = new ArrayList<>(set);
        return new ArrayList<>(set);
    }

    public boolean getReportSent(Context context) {
        SharedPreferences settings = context.getSharedPreferences(REPORT_SETTINGS_FILE, 0);

        return settings.getBoolean(REPORT_SENT_STRING, false);
    }

    public int getReportInterval(){
        return REPORT_INTERVAL;
    }

    public int getBatteryCheckStart(){
        return BATTERY_CHECK_START;  // Morning
    }

    public int getBatteryCheckEnd(){
        return BATTERY_CHECK_END;
    }

    public int getBatteryFrequency(){
        return BATTERY_FREQUENCY;
    }

    public int getBatteryThreshold(){
        return BATTERY_THRESHOLD;
    }

    public int getBatteryLow(){
        return BATTERY_LOW;
    }

    public int getReportHour(){
        return REPORT_HOUR;
    }

    public int getLastReportHour(){
        return LAST_REPORT_HOUR;
    }

    public String getMillisDate(Context context) {
        SharedPreferences settings = context.getSharedPreferences(MISSING_REPORT_FILE, 0);

        return settings.getString(MILLIS_DATE_STRING, null);
    }

    public void setMillisDate(Context context, String millisDateValue){
        if(context == null || millisDateValue == null){
            Log.e(TAG, "SetMillisDate: received null objects");
            return;
        }

        SharedPreferences settings = context.getSharedPreferences(MISSING_REPORT_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString(MILLIS_DATE_STRING, millisDateValue);
        editor.apply();

        Log.i(TAG, "Variable " + MILLIS_DATE_STRING + " set " + millisDateValue);
    }

    public void removeMillisDate(Context context){

        SharedPreferences settings = context.getSharedPreferences(MISSING_REPORT_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();

        editor.remove(MILLIS_DATE_STRING);
        editor.apply();

        Log.i(TAG, "Removed variable " + MILLIS_DATE_STRING);
    }
}

