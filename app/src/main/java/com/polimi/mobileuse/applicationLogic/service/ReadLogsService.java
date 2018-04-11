package com.polimi.mobileuse.applicationLogic.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.polimi.mobileuse.logic.log.LogsManager;
import com.polimi.mobileuse.logic.log.SmartphoneLogReader;
import com.polimi.mobileuse.model.phoneReport.Call;
import com.polimi.mobileuse.model.phoneReport.Message;

import java.util.Calendar;
import java.util.List;

public class ReadLogsService extends IntentService {
    private static final String TAG = ReadLogsService.class.getSimpleName();

    private static final String FIRST_ATTEMPT = "firstAttempt";

    public ReadLogsService() {
        super("ReadLogsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "Start service");

        Context context = getApplicationContext();

        long nowMillis = Calendar.getInstance().getTimeInMillis();
        LogsManager logsManager = new LogsManager(this);
        SmartphoneLogReader smartphoneLogReader = new SmartphoneLogReader(this);


        /* Read Smartphone's call and message logs */
        List<Call> callLogList = smartphoneLogReader.readDeviceCallLog();
        List<Message> messageLogList = smartphoneLogReader.readDeviceMessageLog();


        /* Store data into database */
        logsManager.saveCallList(callLogList, nowMillis);
        logsManager.saveMessageList(messageLogList, nowMillis);

        // Read from database, create reports and send it
        Intent createReportIntent = new Intent(context, CreateDailyReportService.class);
        createReportIntent.putExtra(FIRST_ATTEMPT, true);
        context.startService(createReportIntent);

        Log.i(TAG, "End service");
    }

}
