package com.polimi.movecare_r01.logic.observers;

import android.content.Context;
import android.util.Log;

import com.polimi.movecare_r01.dao.preferences.SharedPreferencesManager;
import com.polimi.movecare_r01.logic.notification.ReportSentNotification;

public class JsonSentObserver implements JsonSentObserverInterface {
    private static final String TAG = JsonSentObserver.class.getSimpleName();

    private static final int JSON_EXPECTED = 4;

    private Context context;
    private String simSerialNumber;
    private JsonSentSubject subject;
    private int receivedACK;

    public JsonSentObserver(Context context, String simSerialNumber, JsonSentSubject subject){
        this.context = context;
        this.simSerialNumber = simSerialNumber;
        this.receivedACK = 0;
        this.subject = subject;
        this.subject.attach(this);
    }

    @Override
    public void update() {
        receivedACK++;
        if(receivedACK == JSON_EXPECTED){
            Log.e(TAG, "All ACK received");

            // Set ReportSent to true
            SharedPreferencesManager sharedPreferencesMng = new SharedPreferencesManager();
            sharedPreferencesMng.setReportSent(context, true);

            // Non invierò la notifica, ma cancellerò tutte le notifiche giornaliere presenti
            ReportSentNotification sentNotification = new ReportSentNotification(context);
            sentNotification.setOpenAppSettings();
            sentNotification.send();

            /*NotificationAbstract notifier = new NotificationAbstract();
            // notifier.cancelDailyNotification();
            notifier.createOpenAppNotification(
                    context,
                    "Daily Report Stored",
                    "UserID: "+simSerialNumber,
                    "report_channel_01",
                    "report_completed_channel",
                    "Cannot send report - An error occurred"
            );
            notifier.sendNotification(1011);*/

            // togliere items con time dalla lista
        } else{
            Log.e(TAG, "Not all ACK received");
        }

    }

    @Override
    public void sendAgainUpdate(String timeMillis) {
        receivedACK++;
        if(receivedACK == JSON_EXPECTED){
            Log.e(TAG, "All ACK received");

            // togliere items con time dalla lista
            SharedPreferencesManager sharedPreferencesMng = new SharedPreferencesManager();
            sharedPreferencesMng.removeMissingDateElement(context, timeMillis);

        } else{
            Log.e(TAG, "Not all ACK received");
        }

    }
}
