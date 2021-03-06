package com.polimi.mobileuse.applicationLogic.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.polimi.mobileuse.dao.preferences.SharedPreferencesManager;
import com.polimi.mobileuse.logic.log.LogsManager;
import com.polimi.mobileuse.logic.report.ReportIndicator;
import com.polimi.mobileuse.logic.report.ReportMeasurement;
import com.polimi.mobileuse.model.phoneReport.Call;
import com.polimi.mobileuse.model.phoneReport.InfoReport;
import com.polimi.mobileuse.model.phoneReport.JsonReport;
import com.polimi.mobileuse.model.phoneReport.Message;
import com.polimi.mobileuse.model.phoneReport.PeopleUseCall;
import com.polimi.mobileuse.model.phoneReport.PeopleUseMessage;
import com.polimi.mobileuse.model.phoneReport.SmartphoneUse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CreateMissingReportService extends IntentService {
    private static final String TAG = CreateMissingReportService.class.getSimpleName();

    private Context context;
    public CreateMissingReportService() {
        super("CreateMissingReportService");
    }

    private String simSerialNumber;

    @Override
    protected void onHandleIntent(Intent intent) {

        simSerialNumber = readSimSerialNumber();

        this.context = getApplicationContext();
        Log.i(TAG, "Other Report Attempts");
        SharedPreferencesManager sharedPreferencesMgr = new SharedPreferencesManager();
        List<String> missingReportMillis = sharedPreferencesMgr.getMissingDateList(context);

        List<InfoReport> infoReportList = new ArrayList<>();
        LogsManager logsManager = new LogsManager(context);

        for(String millisString : missingReportMillis){
            infoReportList.add(createReportInformation(context,
                    logsManager,
                    Long.valueOf(millisString)));
        }

        logsManager.closeDatabaseConnection();
        //List<JsonReport> jsonReportList = new ArrayList<>();

        int millisCount = 0;
        for(InfoReport infoReport : infoReportList){
            //jsonReportList.add(createJsonReport(infoReport));
            JsonReport jsonReport = createJsonReport(infoReport);
            if(jsonReport == null){
                Log.e(TAG, "JsonReport is null");
                continue;
            }

            Intent sendReportIntent = new Intent(this, SendMissingReportService.class);
            sendReportIntent.putExtra(sharedPreferencesMgr.getMillisDate(context), missingReportMillis.get(millisCount));
            sendReportIntent.putExtra("SUM", jsonReport.getSumString());
            sendReportIntent.putExtra("SUC", jsonReport.getSucString());
            sendReportIntent.putExtra("SSU", jsonReport.getSsuString());
            sendReportIntent.putExtra("PSU", jsonReport.getPsuString());

            startService(sendReportIntent);

            millisCount++;
        }


        /*
        Intent sendReportIntent = new Intent(this, SendReportService.class);
        //sendReportIntent.putStringArrayListExtra("jsonReportList", jsonReportList);
        sendReportIntent.putExtra("SUM", jsonSUM.toString());
        sendReportIntent.putExtra("SUC", jsonSUC.toString());
        sendReportIntent.putExtra("SSU", jsonSSU.toString());
        sendReportIntent.putExtra("PSU", jsonPSU.toString());
        startService(sendReportIntent);
        */
    }

    private InfoReport createReportInformation(Context context, LogsManager logsManager, long millisDate){

        List<Call> callLogList = logsManager.getCallsByDate(millisDate);
        List<Message> messageLogList = logsManager.getMessagesByDate(millisDate);

        // Query database and retrieve useful information
        SmartphoneUse smartphoneUse = logsManager.getSmartphoneUse(millisDate);
        List<PeopleUseCall> peopleUseCall = logsManager.getPeopleUseCall(millisDate);
        List<PeopleUseMessage> peopleUseMessage = logsManager.getPeopleUseMessage(millisDate);

        return new InfoReport(callLogList, messageLogList, smartphoneUse, peopleUseCall, peopleUseMessage);
    }

    private JsonReport createJsonReport(InfoReport ir){

        ReportMeasurement reportMeasurement = new ReportMeasurement(context);
        ReportIndicator reportIndicator = new ReportIndicator(context);

        JsonReport jsonReport = null;
        try {
            JSONObject jsonSUC = reportMeasurement.createReportSUC(ir.getCallList());
            JSONObject jsonSUM = reportMeasurement.createReportSUM(ir.getMessageList());

            JSONObject jsonSSU = reportIndicator.createReportSSU(ir.getSmartphoneUse());
            JSONObject jsonPSU = reportIndicator.createReportPSU(ir.getPeopleUseCall(),
                    ir.getPeopleUseMessage());

            jsonReport = new JsonReport(jsonSUM, jsonSUC, jsonSSU, jsonPSU);

        } catch (JSONException e) {
            Log.e(TAG, "Error: Smartphone Report cannot be created");
            e.printStackTrace();
        }
        return jsonReport;
    }

    private String readSimSerialNumber(){
        TelephonyManager tMgr =(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        return tMgr.getSimSerialNumber();
    }

}
