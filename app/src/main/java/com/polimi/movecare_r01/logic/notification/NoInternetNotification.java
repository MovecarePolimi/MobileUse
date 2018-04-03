package com.polimi.movecare_r01.logic.notification;


import android.content.Context;

import com.polimi.movecare_r01.R;

public class NoInternetNotification extends NotificationAbstract {

    public NoInternetNotification(Context ctx) {
        this.context = ctx;
        this.title = context.getString(R.string.reportErrorTitle);
        this.description = context.getString(R.string.noInternetMessage);
        this.channelID = "internet_channel_01";
        this.channelName = "internet_channel";
        this.channelDescription = context.getString(R.string.noInternetMessage);
        this.notificationID = 2022;

        createNotification(ctx, title, description, channelID, channelName, channelDescription);
    }
}