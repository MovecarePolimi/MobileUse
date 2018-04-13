package com.polimi.mobileuse.logic.report;


import android.content.Context;
import android.util.Log;

import com.polimi.mobileuse.dao.preferences.LoginPreferences;
import com.polimi.mobileuse.model.phoneReport.PeopleUseCall;
import com.polimi.mobileuse.model.phoneReport.PeopleUseMessage;
import com.polimi.mobileuse.model.phoneReport.SmartphoneUse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ReportIndicator extends ReportAbstract {

    private static final String TAG = ReportIndicator.class.getSimpleName();

    private static final String CALLS               = "calls";
    private static final String CALLS_IN            = "calls_IN";
    private static final String CALLS_OUT           = "calls_OUT";
    private static final String CALLS_DUR           = "calls_duration";
    private static final String CALLS_DUR_IN        = "calls_duration_IN";
    private static final String CALLS_DUR_OUT       = "calls_duration_OUT";
    private static final String CALLS_PEOPLE        = "calls_people";
    private static final String CALLS_PEOPLE_IN     = "calls_people_IN";
    private static final String CALLS_PEOPLE_OUT    = "calls_people_OUT";

    private static final String MESS                = "messages";
    private static final String MESS_IN             = "messages_IN";
    private static final String MESS_OUT            = "messages_OUT";
    private static final String MESS_PEOPLE         = "messages_people";
    private static final String MESS_PEOPLE_IN      = "messages_people_IN";
    private static final String MESS_PEOPLE_OUT     = "messages_people_OUT";

    private static final String LOG                 = "log";
    private static final String NUMBER              = "number";
    private static final String TOTAL               = "total";
    private static final String TYPE                = "type";
    private static final String DURATION            = "duration";

    private static final String INT                 = "integer";
    private static final String SEC                 = "seconds";

    public ReportIndicator(Context context){
        LoginPreferences log = new LoginPreferences();
        this.context = context;
        this.userID = log.getVariable(context, LoginPreferences.Variable.UUID);
        this.userID = userID;
        this.code = Code.icode;
    }

    // Summary Smartphone Use
    public JSONObject createReportSSU(SmartphoneUse smartphoneUse) throws JSONException {
        String codeValue = "SSU";

        JSONObject reportSSU = new JSONObject();

        reportSSU.put(USER_ID, userID);
        reportSSU.put(code.name(), codeValue);
        reportSSU.put(TIME, createDatestamp());

        reportSSU.put(DATA, createItemsSSU(smartphoneUse));

        return reportSSU;
    }

    // People Smartphone Use
    public JSONObject createReportPSU(List<PeopleUseCall> peopleUseCallList,
                                      List<PeopleUseMessage> peopleUseMessageList) throws JSONException {
        String codeValue = "PSU";

        JSONObject reportPSU = new JSONObject();

        reportPSU.put(USER_ID, userID);
        reportPSU.put(code.name(), codeValue);
        reportPSU.put(TIME, createDatestamp());

        reportPSU.put(DATA, createItemsPSU(peopleUseCallList, peopleUseMessageList));

        return reportPSU;
    }

    private JSONObject createItemsSSU(SmartphoneUse su) throws JSONException {

        JSONArray items = new JSONArray();

        if(su == null){
            Log.w(TAG, "Method createItemsSSU: null object");
        }
        else{
            items.put(createSingleElement(CALLS, su.getCalls(), INT));
            items.put(createSingleElement(CALLS_IN, su.getCallsIN(), INT));
            items.put(createSingleElement(CALLS_OUT, su.getCallsOUT(), INT));

            items.put(createSingleElement(CALLS_DUR, su.getCallsDuration(), SEC));
            items.put(createSingleElement(CALLS_DUR_IN, su.getCallsDurationIN(), SEC));
            items.put(createSingleElement(CALLS_DUR_OUT, su.getCallsDurationOUT(), SEC));

            items.put(createSingleElement(CALLS_PEOPLE, su.getCallsPeople(), INT));
            items.put(createSingleElement(CALLS_PEOPLE_IN, su.getCallsPeopleIN(), INT));
            items.put(createSingleElement(CALLS_PEOPLE_OUT, su.getCallsPeopleOUT(), INT));

            items.put(createSingleElement(MESS, su.getMessages(), INT));
            items.put(createSingleElement(MESS_IN, su.getMessagesIN(), INT));
            items.put(createSingleElement(MESS_OUT, su.getMessagesOUT(), INT));

            items.put(createSingleElement(MESS_PEOPLE, su.getMessagesPeople(), INT));
            items.put(createSingleElement(MESS_PEOPLE_IN, su.getMessagesPeopleIN(), INT));
            items.put(createSingleElement(MESS_PEOPLE_OUT, su.getMessagesPeopleOUT(), INT));
        }

        JSONObject itemsObject = new JSONObject();
        itemsObject.put(ITEMS, items);

        return itemsObject;
    }

    private JSONObject createItemsPSU(List<PeopleUseCall> pucList,
                                      List<PeopleUseMessage> pumList) throws JSONException {

        JSONArray items = new JSONArray();

        if(pucList == null || pucList.size() == 0){
            Log.w(TAG, "Method createItemsPSU: PeopleUseCall null or empty list");
        }
        else{
            for(PeopleUseCall c : pucList){
                items.put(createPeopleUseCallSingleElement(c));
            }
        }

        if(pumList == null || pumList.size() == 0){
            Log.w(TAG, "Method createItemsPSU: PeopleUseMessage null or empty list");
        }
        else{
            for(PeopleUseMessage m : pumList){
                items.put(createPeopleUseMessageSingleElement(m));
            }
        }
        JSONObject itemsObject = new JSONObject();
        itemsObject.put(ITEMS, items);

        return itemsObject;
    }

    private JSONObject createSingleElement(String name, int value, String unit) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(NAME, name);
        jsonObject.put(VALUE, value);
        jsonObject.put(UNIT, unit);

        return jsonObject;
    }


    private JSONObject createPeopleUseCallSingleElement(PeopleUseCall puc) throws JSONException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(LOG, puc.getLog());
        jsonObject.put(NUMBER, puc.getNumber());
        jsonObject.put(TOTAL, puc.getTotal());
        jsonObject.put(TYPE, puc.getType());
        jsonObject.put(DURATION, puc.getDuration());

        return jsonObject;
    }

    private JSONObject createPeopleUseMessageSingleElement(PeopleUseMessage pum) throws JSONException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(LOG, pum.getLog());
        jsonObject.put(NUMBER, pum.getNumber());
        jsonObject.put(TOTAL, pum.getTotal());
        jsonObject.put(TYPE, pum.getType());

        return jsonObject;
    }
}
