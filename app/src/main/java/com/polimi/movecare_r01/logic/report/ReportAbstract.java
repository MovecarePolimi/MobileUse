package com.polimi.movecare_r01.logic.report;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public abstract class ReportAbstract {

    protected static final String USER_ID             = "userid";
    protected static final String TIME                = "time";
    protected static final String TEMPORALITY         = "temporality";
    protected static final String DT                  = "dt";
    protected static final String T                   = "t";
    protected static final String T0                  = "t0";
    protected static final String T1                  = "t1";
    protected static final String DATA                = "data";
    protected static final String ITEMS               = "items";

    protected static final String NAME                = "name";
    protected static final String VALUE               = "value";
    protected static final String UNIT                = "unit";

    protected static final String NUMBER              = "number";
    protected static final String TYPE                = "type";
    protected static final String DURATION            = "duration";


    String userID;
    enum Temporality{
        datestamp,
        timestamp,
        timeinterval;
    }

    enum Code{
        mcode,
        icode
    };

    Code code;

    protected JSONObject createDatestamp() throws JSONException {

        JSONObject time = new JSONObject();

        time.put(TEMPORALITY, Temporality.datestamp);
        time.put(DT, getDatestamp());

        return time;
    }

    // Method currently not used (maybe in the future)
    protected JSONObject createTimestamp() throws JSONException {

        JSONObject time = new JSONObject();

        time.put(TEMPORALITY, Temporality.timestamp);
        // Values to be changed
        time.put(T, 1494256770.105);

        return time;
    }

    // Method currently not used (maybe in the future)
    protected JSONObject createTimeinterval() throws JSONException {

        JSONObject time = new JSONObject();

        time.put(TEMPORALITY, Temporality.timeinterval);
        // Values to be changed
        time.put(T0, 1494256770.105);
        time.put(T1, 1234355660.205);

        return time;
    }

    // return date as "2017-09-25"
    private String getDatestamp(){

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(cal.getTime());
    }
}
