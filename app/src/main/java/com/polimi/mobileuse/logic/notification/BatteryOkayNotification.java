package com.polimi.mobileuse.logic.notification;

import android.content.Context;

import com.polimi.mobileuse.R;

public class BatteryOkayNotification extends NotificationAbstract {

    public BatteryOkayNotification(Context ctx) {
        this.context = ctx;
        this.title = context.getString(R.string.batteryOkTitle);
        this.description = context.getString(R.string.batteryOkMessage);
        this.channelID = "okay_battery_channel_01";
        this.channelName = "okay_battery_channel";
        this.channelDescription = context.getString(R.string.batteryOkMessage);
        this.notificationID = 8088;

        createNotification(ctx, title, description, channelID, channelName, channelDescription);
    }

}