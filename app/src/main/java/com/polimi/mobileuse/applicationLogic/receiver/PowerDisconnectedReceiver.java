package com.polimi.mobileuse.applicationLogic.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.polimi.mobileuse.R;
import com.polimi.mobileuse.dao.preferences.SharedPreferencesManager;
import com.polimi.mobileuse.logic.battery.BatteryChecker;
import com.polimi.mobileuse.ui.MainActivity;

public class PowerDisconnectedReceiver extends BroadcastReceiver {
    private static final String TAG = PowerDisconnectedReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(this.getClass().getSimpleName(), "Start Receiver");

        // Set ReportSent to false
        SharedPreferencesManager sharedPreferencesMng = new SharedPreferencesManager();
        if(sharedPreferencesMng.getReportSent(context)){
            sharedPreferencesMng.setReportSent(context, false);
        }

        BatteryChecker batteryChecker = null;
        try {
            batteryChecker = new BatteryChecker(context);
        } catch (Exception e) {
            Log.e(TAG, "Exception caught: cannot read battery level or charging state");
            e.printStackTrace();
            return;
        }

        if (batteryChecker.isUnderThreshold()){
            Log.v(TAG, "Battery under 40%");

            setMainActivityStrings(context.getString(R.string.recharge_message), true);
            //Toast.makeText(context, "Stop charging: sotto 40%", Toast.LENGTH_LONG).show();
        } else{
            Log.v(TAG, "Battery over 40%");

            setMainActivityStrings(context.getString(R.string.app_running), false);
            //Toast.makeText(context, "Stop charging: sopra 40% ", Toast.LENGTH_LONG).show();
        }

        Log.i(this.getClass().getSimpleName(), "End Receiver");
    }

    private void setMainActivityStrings(String text, boolean isWarning){
        try {
            if (MainActivity.getInst() != null)
                MainActivity.getInst().updateUIStrings(text, isWarning);
        } catch (Exception e) {
            Log.e(TAG, "Exception caught: cannot update UI strings");
            e.printStackTrace();
        }
    }

}
