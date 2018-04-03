package com.polimi.movecare_r01.dao.preferences;


import android.content.Context;
import android.content.SharedPreferences;

public class ReportPreferences {

    private static final String TAG = ReportPreferences.class.getSimpleName();

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

    public boolean getReportSent(Context context) {
        SharedPreferences settings = context.getSharedPreferences(REPORT_SETTINGS_FILE, 0);

        return settings.getBoolean(REPORT_SENT_STRING, false);
    }

    public int getReportInterval(){
        return REPORT_INTERVAL;
    }


    // TO BE COMPLETED IN THE FUTURE
}
