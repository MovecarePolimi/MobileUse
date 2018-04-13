package com.polimi.mobileuse.logic.observers;

import android.content.Context;
import android.util.Log;

import com.polimi.mobileuse.dao.preferences.SharedPreferencesManager;
import com.polimi.mobileuse.logic.notification.ReportSentNotification;

public class JsonSentObserver implements JsonSentObserverInterface {
    private static final String TAG = JsonSentObserver.class.getSimpleName();

    private static final int JSON_EXPECTED = 4;

    private Context context;
    private JsonSentSubject subject;
    private int receivedACK;

    public JsonSentObserver(Context context, JsonSentSubject subject){
        this.context = context;
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

            // Not send notification if everything is fine, but clear daily notifications
            ReportSentNotification sentNotification = new ReportSentNotification(context);
            sentNotification.setOpenAppSettings();
            sentNotification.send();


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
