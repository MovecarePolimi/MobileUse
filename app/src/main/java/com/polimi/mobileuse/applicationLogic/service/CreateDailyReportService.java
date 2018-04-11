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

import java.util.Calendar;
import java.util.List;

public class CreateDailyReportService extends IntentService {

    private static final String TAG = CreateDailyReportService.class.getSimpleName();

    private static final String FIRST_ATTEMPT = "firstAttempt";
    private static final String MILLIS_DATE = "millis_date";

    private String simSerialNumber;

    public CreateDailyReportService() {
        super("CreateDailyReportService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "Start service");

        Context context = getApplicationContext();

        simSerialNumber = readSimSerialNumber();

        boolean firstAttempt = intent.getBooleanExtra(FIRST_ATTEMPT, false);

        createDailyReport(context);

        if(firstAttempt){
            SharedPreferencesManager sharedPreferencesMgr = new SharedPreferencesManager();
            List<String> missingReportDate = sharedPreferencesMgr.getMissingDateList(context);

            if (missingReportDate != null && !missingReportDate.isEmpty()){
                startService(new Intent(context, CreateMissingReportService.class));
            } else{
                Log.e(TAG, "No missed report found");
            }
        }

        //createAllMissingReports(context);

        Log.i(this.getClass().getSimpleName(), "End service");
    }

    private void createDailyReport(Context context){
        LogsManager logsManager = new LogsManager(context);

        InfoReport infoReport = createReportInformation(context,
                                                        logsManager,
                                                        Calendar.getInstance().getTimeInMillis());

        JsonReport jsonReport = createJsonReport(infoReport);

        Intent sendReportIntent = new Intent(this, SendReportService.class);

        //sendReportIntent.putExtra(MILLIS_DATE, timeMillis);
        sendReportIntent.putExtra("SUM", jsonReport.getSumString());
        sendReportIntent.putExtra("SUC", jsonReport.getSucString());
        sendReportIntent.putExtra("SSU", jsonReport.getSsuString());
        sendReportIntent.putExtra("PSU", jsonReport.getPsuString());
        startService(sendReportIntent);

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

        ReportMeasurement reportMeasurement = new ReportMeasurement(simSerialNumber);
        ReportIndicator reportIndicator = new ReportIndicator(simSerialNumber);

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

    private void printDatabase(List<Call> listaC, List<Message> listaM){
        if(listaC != null){
            for(Call c : listaC){
                Log.e(TAG, c.toString());
            }
        }
        else{
            Log.e(TAG, "Lista Call NULL");
        }

        if(listaM != null){
            for(Message m : listaM){
                Log.e(TAG, m.toString());
            }
        }
        else{
            Log.e(TAG, "Lista Message NULL");
        }
    }

    private String readSimSerialNumber(){
        TelephonyManager tMgr =(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
         return tMgr.getSimSerialNumber();
    }

}
