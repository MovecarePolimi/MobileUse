package com.polimi.movecare_r01.logic.battery;


import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import com.polimi.movecare_r01.dao.preferences.SharedPreferencesManager;

public class BatteryChecker {
    private static final String TAG = BatteryChecker.class.getSimpleName();

    private Intent batteryStatus;
    private int batteryLevel;
    private int chargingStatus;

    public BatteryChecker(Context context) throws Exception {
        this.batteryStatus = context
                .registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        if(batteryStatus != null){
            batteryLevel = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            chargingStatus = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        } else{
            throw new Exception("BatteryStatus Null Exception");
        }
    }

    public int getBatteryLevel(){
        return batteryLevel;
    }

    public boolean isCharging() {
        return chargingStatus == BatteryManager.BATTERY_STATUS_CHARGING ||
                chargingStatus == BatteryManager.BATTERY_STATUS_FULL;
    }

    public boolean isUnderThreshold(){

        SharedPreferencesManager sharedPreferencesMgr = new SharedPreferencesManager();
        int batteryThreshold = sharedPreferencesMgr.getBatteryThreshold();

        return batteryLevel <= batteryThreshold;
    }

    public boolean isCriticalLevel(){
        return batteryLevel <= 10;
    }

}
