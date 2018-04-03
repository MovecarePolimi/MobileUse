package com.polimi.movecare_r01.applicationLogic.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.polimi.movecare_r01.applicationLogic.service.ReadLogsService;
import com.polimi.movecare_r01.dao.preferences.SharedPreferencesManager;

public class ReportAlarmReceiver extends BroadcastReceiver {

    private static final String TAG = ReportAlarmReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Start Receiver");

        // Check if daily report has been sent yet:
        // Useful when continuously open the app at REPORT HOUR
        SharedPreferencesManager sharedPreferencesMgr = new SharedPreferencesManager();
        if(sharedPreferencesMgr.getReportSent(context)){
            Log.e(TAG, "Report Sent yet");

            return;
        }

        // Read from call and message logs and store data into database
        context.startService(new Intent(context, ReadLogsService.class));

        Log.i(TAG, "End Receiver");
    }
}
