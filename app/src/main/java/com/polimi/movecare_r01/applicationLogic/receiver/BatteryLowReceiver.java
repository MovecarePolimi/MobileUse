package com.polimi.movecare_r01.applicationLogic.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.polimi.movecare_r01.R;
import com.polimi.movecare_r01.logic.notification.BatteryLowNotification;
import com.polimi.movecare_r01.logic.notification.BatteryOkayNotification;
import com.polimi.movecare_r01.logic.notification.NotificationAbstract;
import com.polimi.movecare_r01.ui.MainActivity;

public class BatteryLowReceiver extends BroadcastReceiver {

    private static final String TAG = BatteryLowReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        String intentAction = intent.getAction();

        if(Intent.ACTION_BATTERY_LOW.equalsIgnoreCase(intentAction)){
            Log.e(TAG, "Battery LOW Receiver");
            //Toast.makeText(context, "Battery LOW Receiver", Toast.LENGTH_LONG).show();

            BatteryLowNotification lowNotification = new BatteryLowNotification(context);
            lowNotification.send();

            // Finish MainActivity and close the application
            MainActivity.getInst().finish();


        } else if(Intent.ACTION_BATTERY_OKAY.equalsIgnoreCase(intentAction)){
            Log.e(TAG, "Battery OKAY Receiver");
            //Toast.makeText(context, "Battery OKAY Receiver", Toast.LENGTH_LONG).show();

            BatteryOkayNotification okayNotification = new BatteryOkayNotification(context);
            okayNotification.setOpenAppSettings();
            okayNotification.send();

            // Restart the activity
            Intent i = new Intent(context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);

        } else{
            Log.e(TAG, "Error, cannot be here");
        }

    }

}
