package com.polimi.mobileuse.model.phoneReport;


import org.json.JSONObject;


public class JsonReport{

    private JSONObject sum; // Single User Message
    private JSONObject suc; // Single User Call
    private JSONObject ssu; // Summary Smartphone Use
    private JSONObject psu; // People Smartphone Use

    public JsonReport(JSONObject sum, JSONObject suc, JSONObject ssu, JSONObject psu){
        this.sum = sum;
        this.suc = suc;
        this.ssu = ssu;
        this.psu = psu;
    }


    public String getSumString() {
        return sum.toString();
    }

    public String getSucString() {
        return suc.toString();
    }

    public String getSsuString() {
        return ssu.toString();
    }

    public String getPsuString() {
        return psu.toString();
    }
}
