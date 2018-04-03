package com.polimi.movecare_r01.logic.notification;


import android.content.Context;

import com.polimi.movecare_r01.R;

public class ServerErrorNotification extends NotificationAbstract {

    public ServerErrorNotification(Context ctx) {
        this.context = ctx;
        this.title = context.getString(R.string.reportErrorTitle);
        this.description = context.getString(R.string.sendReportError);
        this.channelID = "server_channel_01";
        this.channelName = "server_channel";
        this.channelDescription = context.getString(R.string.sendReportError);
        this.notificationID = 2022;

        createNotification(ctx, title, description, channelID, channelName, channelDescription);
    }
}