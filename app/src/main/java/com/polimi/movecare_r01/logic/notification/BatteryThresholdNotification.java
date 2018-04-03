package com.polimi.movecare_r01.logic.notification;

import android.content.Context;

import com.polimi.movecare_r01.R;


public class BatteryThresholdNotification extends NotificationAbstract {
    public BatteryThresholdNotification(Context ctx) {
        this.context = ctx;
        this.title = context.getString(R.string.batteryThresholdTitle);
        this.description = context.getString(R.string.batteryThresholdMessage);
        this.channelID = "threshold_battery_channel_01";
        this.channelName = "threshold_battery_channel";
        this.channelDescription = context.getString(R.string.batteryThresholdMessage);
        this.notificationID = 9099;

        createNotification(ctx, title, description, channelID, channelName, channelDescription);
    }

}