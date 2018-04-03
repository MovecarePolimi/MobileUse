package com.polimi.movecare_r01.applicationLogic.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.polimi.movecare_r01.ui.MainActivity;

public class BootCompletedReceiver extends BroadcastReceiver {

    private static final String TAG = BootCompletedReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i(TAG, "Movecare app is going to start soon");

        Intent i = new Intent(context, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);

    }
}
