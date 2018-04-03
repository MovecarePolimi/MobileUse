package com.polimi.movecare_r01.logic.notification;


import android.content.Context;

import com.polimi.movecare_r01.R;

public class ReportSentNotification extends NotificationAbstract {

    public ReportSentNotification(Context ctx) {
        this.context = ctx;
        this.title = "Daily Report Stored";
        this.description = "Report sent and stored";
        this.channelID = "report_channel_01";
        this.channelName = "report_channel";
        this.channelDescription = "Report sent and stored";
        this.notificationID = 2022;

        createNotification(ctx, title, description, channelID, channelName, channelDescription);
    }
}