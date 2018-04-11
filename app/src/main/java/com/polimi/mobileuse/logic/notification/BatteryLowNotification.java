package com.polimi.mobileuse.logic.notification;

import android.content.Context;

import com.polimi.mobileuse.R;


public class BatteryLowNotification extends NotificationAbstract {
    public BatteryLowNotification(Context ctx) {
        this.context = ctx;
        this.title = context.getString(R.string.batteryLowTitle);
        this.description = context.getString(R.string.batteryLowMessage);
        this.channelID = "low_battery_channel_01";
        this.channelName = "low_battery_channel";
        this.channelDescription = context.getString(R.string.batteryLowMessage);
        this.notificationID = 8088;

        createNotification(ctx, title, description, channelID, channelName, channelDescription);
    }

}
