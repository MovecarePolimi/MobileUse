package com.polimi.movecare_r01.logic.log;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.polimi.movecare_r01.model.phoneReport.Call;
import com.polimi.movecare_r01.model.phoneReport.Message;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SmartphoneLogReader {
    private static final String TAG = SmartphoneLogReader.class.getSimpleName();

    private Context context;
    private static final int MIN_NUMBER_LENGTH = 10;

    public SmartphoneLogReader(Context context) {
        this.context = context;
    }

    public List<Call> readDeviceCallLog() {

        Log.v(TAG, "Method readDeviceCallLog: start");

        String[] projection = new String[]{
                CallLog.Calls._ID,
                CallLog.Calls.DATE,   // millis
                CallLog.Calls.DURATION,
                CallLog.Calls.TYPE,
                CallLog.Calls.NUMBER
        };

        // Defines a string to contain the selection clause
        String selection = CallLog.Calls.DATE + " BETWEEN ? AND ? ";

        // Initializes an array to contain selection arguments
        String[] args = {createMidnight(), createRightNow()};

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Method readDeviceCallLog: Call Permission Required");
            return null;
        }

        List<Call> callSet = new ArrayList<>();

        try (
                Cursor cursor = context.getContentResolver().query(
                        CallLog.Calls.CONTENT_URI,              // The call URi to query
                        projection,                             // The column to return (projection)
                        selection,                              // The values for the WHERE clause (selection)
                        args,                                   // Selection args
                        CallLog.Calls.DATE + " DESC"            // The sort order
                )
        ) {
            if (cursor == null || cursor.getCount() <= 0) {
                Log.i(TAG, "Method readDeviceCallLog: No call found");
                return null;
            }

            while (cursor.moveToNext()) {
                // Check duration
                if(cursor.getInt(2) == 0){
                    Log.e(TAG, "Method readDeviceCallLog: call duration is 0");
                    continue;
                }

                String currentNumber = cursor.getString(4);
                int number = getValidNumber(currentNumber);

                if(number == -1){
                    Log.e(TAG, "Method readDeviceCallLog: number not valid (advertise, operator)");
                    continue;
                }

                int SMARTPHONE_ID = 254;

                Call call = new Call(
                        cursor.getInt(0),     // ID
                        cursor.getLong(1),    // date
                        cursor.getInt(2),     // duration
                        cursor.getInt(3),     // type
                        //cursor.getString(4).substring(numberSize-4,numberSize),  // number (4 last numbers)
                        number,
                        SMARTPHONE_ID);
                callSet.add(call);
                Log.e("****", call.toString());
            }
        }

        Log.v(TAG, "Method readDeviceCallLog: end");
        return callSet;
    }

    // if number is valid (no advertise, phone operator), returns the last 4 digits, otherwise -1
    private int getValidNumber(String number){

        if(number.length()<MIN_NUMBER_LENGTH){
            Log.i(TAG, "Method numberValidFormat: number too short");
            return -1;
        }

        if(number.charAt(0) == '+') {
            number = number.substring(1, number.length());
        }

        int returnNumber;
        try{
            long l = Long.parseLong(number) % 10000;
            returnNumber = (int) l;

        } catch (NumberFormatException e){
            Log.i(TAG, "Method numberValidFormat: number not numerical");
            returnNumber = -1;
        }
        return returnNumber;

    }

    private boolean contactExists(String number) {

        if(number.length()<MIN_NUMBER_LENGTH){
            Log.e(TAG, "Method contactExists: number too short");
            return false;
        }

        Uri lookupUri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number));
        String[] mPhoneNumberProjection = {
                ContactsContract.PhoneLookup._ID,
                ContactsContract.PhoneLookup.NUMBER,
                ContactsContract.PhoneLookup.DISPLAY_NAME };
        Cursor cur = context.getContentResolver().query(lookupUri,mPhoneNumberProjection, null, null, null);
        try {
            if (cur != null && cur.moveToFirst()) {
                return true;
            }
        } finally {
            if (cur != null)
                cur.close();
        }
        return false;
    }


    public List<Message> readDeviceMessageLog() {

        Log.v(TAG, "Method readDeviceMessageLog: start");

        String[] projection = new String[]{
                Telephony.Sms._ID,
                Telephony.Sms.DATE,
                Telephony.Sms.TYPE,
                Telephony.Sms.ADDRESS,
        };

        // Defines a string to contain the selection clause
        String selection = Telephony.Sms.DATE + " BETWEEN ? AND ? ";

        // Initializes an array to contain selection arguments
        String[] args = {createMidnight(), createRightNow()};

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "SMS Permission Required");
            return null;
        }

        List<Message> messageSet = new ArrayList<>();

        try (
                Cursor cursor = context.getContentResolver().query(
                        Telephony.Sms.CONTENT_URI,              // The call URi to query
                        projection,                             // The column to return (projection)
                        selection,                              // The values for the WHERE clause (selection)
                        args,                                   // Selection args
                        null                                    // The sort order
                )
        ) {
            if (cursor == null || cursor.getCount() <= 0) {
                Log.i(TAG, "Method readDeviceMessageLog: No message found");
                return null;
            }

            while (cursor.moveToNext()) {

                String currentNumber = cursor.getString(3);
                int number = getValidNumber(currentNumber);

                if(number == -1){
                    Log.e(TAG, "Method readDeviceMessageLog: number not valid (advertise, operator)");
                    continue;
                }


                int SMARTPHONE_ID = 254;
                Message message = new Message(
                        cursor.getInt(0),       // ID
                        cursor.getLong(1),      // date
                        cursor.getInt(2),       // type
                        number,                 // number (4 last numbers)
                        SMARTPHONE_ID);
                messageSet.add(message);
                Log.e("****", message.toString());
            }
        }

        Log.v(TAG, "Method readDeviceMessageLog: start");
        return messageSet;
    }


    private String createRightNow() {
        Calendar rightNow = Calendar.getInstance();

        return String.valueOf(rightNow.getTimeInMillis());
    }

    private String createMidnight() {
        int hour = 0;
        int minutes = 0;

        Calendar yesterday = Calendar.getInstance();
        yesterday.set(
                yesterday.get(Calendar.YEAR),
                yesterday.get(Calendar.MONTH),
                yesterday.get(Calendar.DAY_OF_MONTH),
                hour,
                minutes);

        return String.valueOf(yesterday.getTimeInMillis());
    }
}
