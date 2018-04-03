package com.polimi.movecare_r01.logic.notification;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.polimi.movecare_r01.R;
import com.polimi.movecare_r01.ui.MainActivity;

public abstract class NotificationAbstract {
    private static final String TAG = NotificationAbstract.class.getSimpleName();

    protected Context context;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder;
    int notificationID;

    protected String title;
    String description;
    String channelID;
    String channelName;
    String channelDescription;

    void createNotification(Context ctx, String title, String desc, String channelID, String channelName, String channelDesc){
        this.context = ctx;
        this.title = title;
        this.description = desc;
        this.channelID = channelID;
        this.channelName = channelName;
        this.channelDescription = channelDesc;

        this.notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        this.builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.mc_launcher)
                        .setContentTitle(title)
                        .setContentText(description)
                        .setDefaults(NotificationCompat.DEFAULT_SOUND)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        ;

        /*if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(R.mipmap.mc_launcher);
            builder.setColor(Color.GREEN);
        } else {
            builder.setSmallIcon(R.mipmap.mc_launcher);
        }*/


        // API 26 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.v("Versione", "Android 8");

            NotificationChannel mChannel = new NotificationChannel(
                    channelID, channelName, NotificationManager.IMPORTANCE_HIGH);

            mChannel.setDescription(channelDescription);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationManager.createNotificationChannel(mChannel);
        } else{
            // API 25 or lower
            Log.v("Versione", "Android < 8");
            builder.setPriority(NotificationCompat.PRIORITY_MAX)
                    .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                    .setLights(Color.GREEN, 3000, 3000)
            ;
        }
    }


    public void setAutoCancel(boolean autoCancel){
        this.builder.setAutoCancel(autoCancel);
    }

    public void setOpenWifiSettings(){

        Intent openWifiIntent = new Intent();
        openWifiIntent.setAction("android.settings.WIFI_SETTINGS");

        // for Android 8
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            openWifiIntent.putExtra("android.provider.extra.APP_PACKAGE", context.getPackageName());
        } else{
            //for Android 5-7
            openWifiIntent.putExtra("app_package", context.getPackageName());
            openWifiIntent.putExtra("app_uid", context.getApplicationInfo().uid);
        }


        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                openWifiIntent, 0);

        builder.setContentIntent(pendingIntent);

    }

    public void setOpenAppSettings(){
        Intent openAppIntent = new Intent(context, MainActivity.class);

        openAppIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                openAppIntent, 0);

        builder.setContentIntent(pendingIntent);

    }

    public void send(){
        notificationManager.notify(notificationID, builder.build());
    }

    /*public void createOpenWifiNotification(Context context,
                                           String title,
                                           String description,
                                           String channelID,
                                           String channelName,
                                           String channelDescription){

        this.context = context;
        this.notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        cancelAllNotification();

        this.builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.mc_launcher)
                        .setContentTitle(title)
                        .setContentText(description)
                        .setDefaults(NotificationCompat.DEFAULT_SOUND)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                ;

        // API 26 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.v("Versione", "Android 8");

            NotificationChannel mChannel = new NotificationChannel(
                    channelID, channelName, NotificationManager.IMPORTANCE_HIGH);

            mChannel.setDescription(channelDescription);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationManager.createNotificationChannel(mChannel);
        } else{
            // API 25 or lower
            Log.v("Versione", "Android < 8");
            builder.setPriority(NotificationCompat.PRIORITY_MAX)
                    .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                    .setLights(Color.GREEN, 3000, 3000)
            ;
        }

        //builder.setAutoCancel(true);
        setOpenWifiSettings();
    }

    public void createOpenAppNotification(Context context,
                                           String title,
                                           String description,
                                           String channelID,
                                           String channelName,
                                           String channelDescription){

        this.context = context;
        this.notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);



        cancelAllNotification();

        this.builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.mc_launcher)
                        .setContentTitle(title)
                        .setContentText(description)
                        .setDefaults(NotificationCompat.DEFAULT_SOUND)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        ;

        // API 26 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.v("Versione", "Android 8");

            NotificationChannel mChannel = new NotificationChannel(
                    channelID, channelName, NotificationManager.IMPORTANCE_HIGH);

            mChannel.setDescription(channelDescription);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationManager.createNotificationChannel(mChannel);
        } else{
            // API 25 or lower
            Log.v("Versione", "Android < 8");
            builder.setPriority(NotificationCompat.PRIORITY_MAX)
                    .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                    .setLights(Color.GREEN, 3000, 3000)
            ;
        }

        builder.setAutoCancel(true);
        setOpenAppSettings();
    }*/

    /*private void setOpenWifiSettings(){

        Intent openWifiIntent = new Intent();
        openWifiIntent.setAction("android.settings.WIFI_SETTINGS");

        // for Android 8
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            openWifiIntent.putExtra("android.provider.extra.APP_PACKAGE", context.getPackageName());
        } else{
            //for Android 5-7
            openWifiIntent.putExtra("app_package", context.getPackageName());
            openWifiIntent.putExtra("app_uid", context.getApplicationInfo().uid);
        }


        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                openWifiIntent, 0);

        builder.setContentIntent(pendingIntent);

    }

    private void setOpenAppSettings(){
        Intent openAppIntent = new Intent(context, MainActivity.class);

        openAppIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                openAppIntent, 0);

        builder.setContentIntent(pendingIntent);

    }*/


    public void cancelAllNotification(){
        try{
            notificationManager.cancelAll();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void cancelDailyNotification(Context context){
        try{
            notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
        } catch (Exception e){
            Log.e(TAG, "Exception caught: cannot cancel daily notifications");
            e.printStackTrace();
        }

    }

    public void cancelNotification(int notificationID){
        notificationManager.cancel(notificationID);
    }
}
