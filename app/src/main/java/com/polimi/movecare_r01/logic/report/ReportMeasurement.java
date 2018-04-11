package com.polimi.movecare_r01.logic.report;

import android.util.Log;

import com.polimi.movecare_r01.model.phoneReport.Call;
import com.polimi.movecare_r01.model.phoneReport.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ReportMeasurement extends ReportAbstract {

    private static final String TAG = ReportMeasurement.class.getSimpleName();

    private static final String SENSOR_ID = "sensorid";
    private static final String SENSOR_ID_SMARTPHONE  = "SENSOR_ID_TO_BE_CHANGED";

    private String sensorID;

    public ReportMeasurement(String userID){
        this.userID = userID;
        this.code = Code.mcode;
    }

    // Single User Message
    public JSONObject createReportSUM(List<Message> messageList) throws JSONException {
        String codeValue = "SUM";
        this.sensorID = SENSOR_ID_SMARTPHONE;

        JSONObject reportSUM = new JSONObject();

        reportSUM.put(USER_ID, userID);
        reportSUM.put(code.name(), codeValue);
        reportSUM.put(SENSOR_ID, sensorID);
        reportSUM.put(TIME, createDatestamp());

        reportSUM.put(DATA, createItemsSUM(messageList));

        return reportSUM;
    }

    // Single User Call
    public JSONObject createReportSUC(List<Call> callList) throws JSONException {
        String codeValue = "SUC";
        this.sensorID = SENSOR_ID_SMARTPHONE;

        JSONObject reportSUC = new JSONObject();

        reportSUC.put(USER_ID, userID);
        reportSUC.put(code.name(), codeValue);
        reportSUC.put(SENSOR_ID, sensorID);
        reportSUC.put(TIME, createDatestamp());

        reportSUC.put(DATA, createItemsSUC(callList));

        return reportSUC;
    }

    private JSONObject createItemsSUM(List<Message> messageList) throws JSONException {

        JSONArray items = new JSONArray();

        if(messageList == null || messageList.isEmpty()){
            Log.w(TAG, "Method createItemsSUM: empty messages list");
        }
        else{
            for (Message m : messageList) {
                items.put(createSingleMessage(m.getNumber(), m.getTypeEnum(), m.getTimeString()));
            }
        }

        JSONObject itemsObject = new JSONObject();
        itemsObject.put(ITEMS, items);

        return itemsObject;
    }

    private JSONObject createItemsSUC(List<Call> callList) throws JSONException {

        JSONArray items = new JSONArray();

        if(callList == null || callList.isEmpty()){
            Log.w(TAG, "Method createItemsSUC: empty calls list");
        }
        else{
            for (Call c : callList) {
                items.put(createSingleCall(c.getNumber(), c.getDuration(), c.getTypeEnum(), c.getTimeString()));
            }
        }

        JSONObject itemsObject = new JSONObject();
        itemsObject.put(ITEMS, items);

        return itemsObject;
    }

    private JSONObject createSingleMessage(int number, String type, String time) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(NUMBER, number);
        jsonObject.put(TYPE, type);
        jsonObject.put(TIME, time);

        return jsonObject;
    }

    private JSONObject createSingleCall(int number, int duration, String type, String time) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(NUMBER, number);
        jsonObject.put(DURATION, duration);
        jsonObject.put(TYPE, type);
        jsonObject.put(TIME, time);

        return jsonObject;
    }
}
