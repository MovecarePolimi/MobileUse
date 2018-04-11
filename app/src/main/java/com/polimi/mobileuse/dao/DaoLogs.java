package com.polimi.mobileuse.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.util.Log;

import com.polimi.mobileuse.model.phoneReport.Call;
import com.polimi.mobileuse.model.phoneReport.Message;
import com.polimi.mobileuse.model.phoneReport.PeopleUseCall;
import com.polimi.mobileuse.model.phoneReport.PeopleUseMessage;
import com.polimi.mobileuse.model.phoneReport.SmartphoneUse;

import java.util.ArrayList;
import java.util.List;


public class DaoLogs extends Dao{
    private static final String TAG = DaoLogs.class.getSimpleName();

    private static final int INCOMING_TYPE = 1;
    private static final int OUTCOMING_TYPE = 2;

    public DaoLogs(Context context) {
        super(context);
    }

    /***** CALLS *****/
    public void storeCallList(List<Call> callSet) throws Exception {
        Log.v(TAG, "Method storeCallList: start");

        if (callSet == null || callSet.isEmpty()) {
            Log.d(TAG, "Method storeCallList: received null or empty List");
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        for (Call c : callSet) {
            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(DatabaseContract.CallTable._ID, c.getId());
            values.put(DatabaseContract.CallTable.DATE_COL1, c.getDateMillis());
            values.put(DatabaseContract.CallTable.DURATION_COL2, c.getDuration());
            values.put(DatabaseContract.CallTable.TYPE_COL3, c.getType());
            values.put(DatabaseContract.CallTable.NUMBER_COL4, c.getNumber());
            values.put(DatabaseContract.CallTable.SMARTPHONE_SESSION_COL5, c.getSmartphoneSession());

            long newRowId = db.insert(
                    DatabaseContract.CallTable.TABLE_NAME,
                    null,
                    values);

            if (newRowId == -1) {
                // An error occurred
                throw new Exception("DBInsertException");
            }
            Log.d(TAG, "Method storeCallList: call stored");
        }
        Log.v(TAG, "Method storeCallList: end");
    }

    public void storeCall(Call c) throws Exception {
        Log.v(TAG, "Method storeCall: start");

        if (c == null) {
            Log.d(TAG, "Method storeCall: null Object received");
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.CallTable._ID, c.getId());
        values.put(DatabaseContract.CallTable.DATE_COL1, c.getDateMillis());
        values.put(DatabaseContract.CallTable.DURATION_COL2, c.getDuration());
        values.put(DatabaseContract.CallTable.TYPE_COL3, c.getType());
        values.put(DatabaseContract.CallTable.NUMBER_COL4, c.getNumber());
        values.put(DatabaseContract.CallTable.SMARTPHONE_SESSION_COL5, c.getSmartphoneSession());

        long newRowId = db.insert(
                DatabaseContract.CallTable.TABLE_NAME,
                null,
                values);

        if (newRowId == -1) {
            throw new Exception("DBInsertException");
        }

        Log.v(TAG, "Method storeCall: end");

    }

    public List<Call> retrieveAllCalls() {
        Log.v(TAG, "Method retrieveAllCalls: start");
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                DatabaseContract.CallTable._ID,
                DatabaseContract.CallTable.DATE_COL1,
                DatabaseContract.CallTable.DURATION_COL2,
                DatabaseContract.CallTable.TYPE_COL3,
                DatabaseContract.CallTable.NUMBER_COL4,
                DatabaseContract.CallTable.SMARTPHONE_SESSION_COL5
        };

        List<Call> resultCallSet = new ArrayList<>();

        try (
                Cursor cursor = db.query(
                        DatabaseContract.CallTable.TABLE_NAME,       // The table to query
                        projection,                                  // The columns to return
                        null,                                        // The columns for the WHERE clause
                        null,                                        // The values for the WHERE clause
                        null,                                        // don't group the rows
                        null,                                        // don't filter by row groups
                        null                                         // The sort order
                )
        ) {
            if (cursor == null || cursor.getCount() <= 0) {
                Log.d(TAG, "Method retrieveAllCalls: No call found by query");
                return null;
            }

            while (cursor.moveToNext()) {
                Call call = new Call(
                        cursor.getInt(0),       // ID
                        cursor.getLong(1),      // Date
                        cursor.getInt(2),       // Duration
                        cursor.getInt(3),       // Type
                        cursor.getInt(4),       // Number
                        cursor.getInt(5)        // Smartphone_ID
                );

                resultCallSet.add(call);
            }
        }

        Log.v(TAG, "Method retrieveAllCalls: end");
        return resultCallSet;
    }

    /*
    public List<Call> retrieveDailyCalls() {
        Log.v(TAG, "Method retrieveDailyCalls: start");
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                DatabaseContract.CallTable._ID,
                DatabaseContract.CallTable.DATE_COL1,
                DatabaseContract.CallTable.DURATION_COL2,
                DatabaseContract.CallTable.TYPE_COL3,
                DatabaseContract.CallTable.NUMBER_COL4,
                DatabaseContract.CallTable.SMARTPHONE_SESSION_COL5
        };

        String selection = "date BETWEEN ? AND ?";

        String[] selectionArgs = {
                getLastMidnight(),
                getRightNow()
        };

        List<Call> resultCallSet = new ArrayList<>();

        try (
                Cursor cursor = db.query(
                        DatabaseContract.CallTable.TABLE_NAME,       // The table to query
                        projection,                                  // The columns to return
                        selection,                                   // The columns for the WHERE clause
                        selectionArgs,                               // The values for the WHERE clause
                        null,                                        // don't group the rows
                        null,                                        // don't filter by row groups
                        null                                         // The sort order
                )
        ) {
            if (cursor == null || cursor.getCount() <= 0) {
                Log.i(TAG, "Method retrieveDailyCalls: No call found by query");
                return null;
            }

            while (cursor.moveToNext()) {
                Call call = new Call(
                        cursor.getInt(0),       // ID
                        cursor.getLong(1),      // Date
                        cursor.getInt(2),       // Duration
                        cursor.getInt(3),       // Type
                        cursor.getInt(4),       // Number
                        cursor.getInt(5)        // Smartphone_ID
                );

                resultCallSet.add(call);
            }
        }

        Log.v(TAG, "Method retrieveDailyCalls: end");
        return resultCallSet;
    }
    */

    public List<Call> retrieveCallsByDay(long dateMillis){
        Log.v(TAG, "Method retrieveCallsByDay: start");
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                DatabaseContract.CallTable._ID,
                DatabaseContract.CallTable.DATE_COL1,
                DatabaseContract.CallTable.DURATION_COL2,
                DatabaseContract.CallTable.TYPE_COL3,
                DatabaseContract.CallTable.NUMBER_COL4,
                DatabaseContract.CallTable.SMARTPHONE_SESSION_COL5
        };

        String selection = "date BETWEEN ? AND ?";

        String[] selectionArgs = {
                getSpecificDayMidnight(dateMillis),
                fromMillisToString(dateMillis)
        };

        List<Call> resultCallSet = new ArrayList<>();

        try (
                Cursor cursor = db.query(
                        DatabaseContract.CallTable.TABLE_NAME,       // The table to query
                        projection,                                  // The columns to return
                        selection,                                   // The columns for the WHERE clause
                        selectionArgs,                               // The values for the WHERE clause
                        null,                                        // don't group the rows
                        null,                                        // don't filter by row groups
                        null                                         // The sort order
                )
        ) {
            if (cursor == null || cursor.getCount() <= 0) {
                Log.d(TAG, "Method retrieveCallsByDay: No call found by query");
                return null;
            }

            while (cursor.moveToNext()) {
                Call call = new Call(
                        cursor.getInt(0),       // ID
                        cursor.getLong(1),      // Date
                        cursor.getInt(2),       // Duration
                        cursor.getInt(3),       // Type
                        cursor.getInt(4),       // Number
                        cursor.getInt(5)        // Smartphone_ID
                );

                resultCallSet.add(call);
            }
        }

        Log.v(TAG, "Method retrieveCallsByDay: end");
        return resultCallSet;
    }

    /***** MESSAGES *****/
    public void storeMessageList(List<Message> messageSet) throws Exception {
        Log.v(TAG, "Method storeMessageList: start");

        if (messageSet == null || messageSet.isEmpty()) {
            Log.d(TAG, "Method storeMessageList: received null or empty List");
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        for (Message m : messageSet) {
            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(DatabaseContract.MessageTable._ID, m.getId());
            values.put(DatabaseContract.MessageTable.DATE_COL1, m.getDateMillis());
            values.put(DatabaseContract.MessageTable.TYPE_COL2, m.getType());
            values.put(DatabaseContract.MessageTable.NUMBER_COL3, m.getNumber());
            values.put(DatabaseContract.MessageTable.SMARTPHONE_SESSION_COL4, m.getSmartphoneSession());

            // Questo ritorna -1
            long newRowId = db.insert(
                    DatabaseContract.MessageTable.TABLE_NAME,
                    null,
                    values);

            if (newRowId == -1) {
                // An error occurred
                throw new Exception("DBInsertException");
            }
            Log.d(TAG, "Method storeMessageList: message stored");
        }
        Log.v(TAG, "Method storeMessageList: end");
    }

    public void storeMessage(Message m) throws Exception {
        Log.v(TAG, "Method storeMessage: start");

        if (m == null) {
            Log.w(TAG, "Method storeMessage: null Object received");
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.MessageTable._ID, m.getId());
        values.put(DatabaseContract.MessageTable.DATE_COL1, m.getDateMillis());
        values.put(DatabaseContract.MessageTable.TYPE_COL2, m.getType());
        values.put(DatabaseContract.MessageTable.NUMBER_COL3, m.getNumber());
        values.put(DatabaseContract.MessageTable.SMARTPHONE_SESSION_COL4, m.getSmartphoneSession());

        long newRowId = db.insert(
                DatabaseContract.MessageTable.TABLE_NAME,
                null,
                values);

        if (newRowId == -1) {
            // An error occurred
            throw new Exception("DBInsertException");
        }
        Log.d(TAG, "Method storeMessageList: message stored");

        Log.v(TAG, "Method storeMessage: end");
    }

    public List<Message> retrieveAllMessages() {

        Log.v(TAG, "Method retrieveAllMessages: start");
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                DatabaseContract.MessageTable._ID,
                DatabaseContract.MessageTable.DATE_COL1,
                DatabaseContract.MessageTable.TYPE_COL2,
                DatabaseContract.MessageTable.NUMBER_COL3,
                DatabaseContract.MessageTable.SMARTPHONE_SESSION_COL4
        };

        List<Message> resultMessageSet = new ArrayList<>();

        try (
                Cursor cursor = db.query(
                        DatabaseContract.MessageTable.TABLE_NAME,    // The table to query
                        projection,                                  // The columns to return
                        null,                                        // The columns for the WHERE clause
                        null,                                        // The values for the WHERE clause
                        null,                                        // don't group the rows
                        null,                                        // don't filter by row groups
                        null                                         // The sort order
                )
        ) {
            if (cursor == null || cursor.getCount() <= 0) {
                Log.d(TAG, "Method retrieveAllMessages: No message found by query");
                return null;
            }

            while (cursor.moveToNext()) {
                Message message = new Message(
                        cursor.getInt(0),       // ID
                        cursor.getLong(1),      // Date
                        cursor.getInt(2),       // Type
                        cursor.getInt(3),       // Number
                        cursor.getInt(4)        // Smartphone_ID
                );

                resultMessageSet.add(message);
            }
        }

        Log.v(TAG, "Method retrieveAllMessages: end");
        return resultMessageSet;
    }

    /*public List<Message> retrieveDailyMessages() {
        Log.v(TAG, "Method retrieveDailyMessages: start");
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                DatabaseContract.MessageTable._ID,
                DatabaseContract.MessageTable.DATE_COL1,
                DatabaseContract.MessageTable.TYPE_COL2,
                DatabaseContract.MessageTable.NUMBER_COL3,
                DatabaseContract.MessageTable.SMARTPHONE_SESSION_COL4
        };

        String selection = "date BETWEEN ? AND ?";

        String[] selectionArgs = {
                getLastMidnight(),
                getRightNow()
        };

        List<Message> resultMessageSet = new ArrayList<>();

        try (
                Cursor cursor = db.query(
                        DatabaseContract.MessageTable.TABLE_NAME,       // The table to query
                        projection,                                  // The columns to return
                        selection,                                   // The columns for the WHERE clause
                        selectionArgs,                               // The values for the WHERE clause
                        null,                                        // don't group the rows
                        null,                                        // don't filter by row groups
                        null                                         // The sort order
                )
        ) {
            if (cursor == null || cursor.getCount() <= 0) {
                Log.i(TAG, "Method retrieveDailyMessages: No message found by query");
                return null;
            }

            while (cursor.moveToNext()) {
                Message message = new Message(
                        cursor.getInt(0),       // ID
                        cursor.getLong(1),      // Date
                        cursor.getInt(2),       // Type
                        cursor.getInt(3),       // Number
                        cursor.getInt(4)        // Smartphone_ID
                );

                resultMessageSet.add(message);
            }
        }

        Log.v(TAG, "Method retrieveDailyMessages: end");
        return resultMessageSet;
    }*/

    public List<Message> retrieveMessagesByDay(long dateMillis){
        Log.v(TAG, "Method retrieveMessagesByDay: start");
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                DatabaseContract.MessageTable._ID,
                DatabaseContract.MessageTable.DATE_COL1,
                DatabaseContract.MessageTable.TYPE_COL2,
                DatabaseContract.MessageTable.NUMBER_COL3,
                DatabaseContract.MessageTable.SMARTPHONE_SESSION_COL4
        };

        String selection = "date BETWEEN ? AND ?";

        String[] selectionArgs = {
                getSpecificDayMidnight(dateMillis),
                fromMillisToString(dateMillis)
        };

        List<Message> resultMessageSet = new ArrayList<>();

        try (
                Cursor cursor = db.query(
                        DatabaseContract.MessageTable.TABLE_NAME,       // The table to query
                        projection,                                  // The columns to return
                        selection,                                   // The columns for the WHERE clause
                        selectionArgs,                               // The values for the WHERE clause
                        null,                                        // don't group the rows
                        null,                                        // don't filter by row groups
                        null                                         // The sort order
                )
        ) {
            if (cursor == null || cursor.getCount() <= 0) {
                Log.d(TAG, "Method retrieveMessagesByDay: No message found by query");
                return null;
            }

            while (cursor.moveToNext()) {
                Message call = new Message(
                        cursor.getInt(0),       // ID
                        cursor.getLong(1),      // Date
                        cursor.getInt(2),       // Type
                        cursor.getInt(3),       // Number
                        cursor.getInt(4)        // Smartphone_ID
                );

                resultMessageSet.add(call);
            }
        }

        Log.v(TAG, "Method retrieveMessagesByDay: end");
        return resultMessageSet;
    }


    /***** SMARTPHONE USE *****/
    public SmartphoneUse retrieveSmartphoneUse(long dateMillis){
        Log.v(TAG, "Method retrieveSmartphoneUse: start");

        SmartphoneUse smartphoneUse = new SmartphoneUse();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        smartphoneUse = getSmartphoneMessagesInfo(db, smartphoneUse, dateMillis);
        smartphoneUse = getSmartphoneCallsInfo(db, smartphoneUse, dateMillis);
        smartphoneUse = getSmartphonePeopleCallsInfo(db, smartphoneUse, dateMillis);
        smartphoneUse = getSmartphonePeopleMessagesInfo(db, smartphoneUse, dateMillis);

        Log.v(TAG, "Method retrieveSmartphoneUse: end");
        return smartphoneUse;
    }

    @Nullable
    private SmartphoneUse getSmartphoneMessagesInfo(SQLiteDatabase db,
                                                    SmartphoneUse smartphoneUse,
                                                    long dateMillis){
        Log.v(TAG, "Method getSmartphoneMessagesInfo: start");

        if(smartphoneUse == null){
            smartphoneUse = new SmartphoneUse();
        }
        if(db == null){
            db = dbHelper.getReadableDatabase();
        }

        String[] projection = {
                DatabaseContract.MessageTable.TYPE_COL2,
                "COUNT(distinct "+DatabaseContract.MessageTable._ID+") as MessagesNumber",
                "COUNT(distinct "+DatabaseContract.MessageTable.NUMBER_COL3+") as MessagesPeople"
        };

        String selection = DatabaseContract.MessageTable.DATE_COL1+
                " BETWEEN ? AND ?";

        /*String[] selectionArgs = {
                getLastMidnight(),
                getRightNow()
        };*/

        String[] selectionArgs = {
                getSpecificDayMidnight(dateMillis),
                fromMillisToString(dateMillis)
        };

        String groupBy = DatabaseContract.MessageTable.TYPE_COL2;

        try (
                Cursor cursor = db.query(
                        DatabaseContract.MessageTable.TABLE_NAME,    // The table to query
                        projection,                                  // The columns to return
                        selection,                                   // The columns for the WHERE clause
                        selectionArgs,                               // The values for the WHERE clause
                        groupBy,                                     // Group the rows
                        null,                                        // don't filter by row groups
                        null                                         // The sort order
                )
        ) {
            if (cursor == null || cursor.getCount() <= 0) {
                Log.d(TAG, "Method retrieveSmartphoneUse > MessagesInfo: No messages found by query");
                return null;
            }

            // At most two rows allowed: Incoming and Outcoming
            if(cursor.getCount() > 2){
                Log.e(TAG, "Method retrieveSmartphoneUse > MessagesInfo: Message query error");
                return null;
            }

            while (cursor.moveToNext()) {
                if(cursor.getInt(0) == INCOMING_TYPE){
                    smartphoneUse.setMessagesIN(cursor.getInt(1));
                    smartphoneUse.setMessagesPeopleIN(cursor.getInt(2));
                } else if(cursor.getInt(0) == OUTCOMING_TYPE){
                    smartphoneUse.setMessagesOUT(cursor.getInt(1));
                    smartphoneUse.setMessagesPeopleOUT(cursor.getInt(2));
                } else{
                    Log.e(TAG, "Unexpected message type: "+ cursor.getInt(0));
                }
            }

            smartphoneUse.setMessages(smartphoneUse.getMessagesIN() + smartphoneUse.getMessagesOUT());
        }

        Log.v(TAG, "Method getSmartphoneMessagesInfo: end");
        return smartphoneUse;
    }

    @Nullable
    private SmartphoneUse getSmartphoneCallsInfo(SQLiteDatabase db,
                                                 SmartphoneUse smartphoneUse,
                                                 long dateMillis){

        if(smartphoneUse == null){
            smartphoneUse = new SmartphoneUse();
        }
        if(db == null){
            db = dbHelper.getReadableDatabase();
        }

        String[] projection = {
                DatabaseContract.CallTable.TYPE_COL3,
                "COUNT(distinct "+DatabaseContract.CallTable._ID+") as CallsNumber",
                "COUNT(distinct "+DatabaseContract.CallTable.NUMBER_COL4+") as CallsPeople",
                "SUM(distinct "+DatabaseContract.CallTable.DURATION_COL2+") as CallsDuration"
        };

        String selection = DatabaseContract.CallTable.DATE_COL1+
                " BETWEEN ? AND ?";

        String[] selectionArgs = {
                getSpecificDayMidnight(dateMillis),
                fromMillisToString(dateMillis)
        };

        String groupBy = DatabaseContract.CallTable.TYPE_COL3;

        try (
                Cursor cursor = db.query(
                        DatabaseContract.CallTable.TABLE_NAME,       // The table to query
                        projection,                                  // The columns to return
                        selection,                                   // The columns for the WHERE clause
                        selectionArgs,                               // The values for the WHERE clause
                        groupBy,                                     // Group the rows
                        null,                                        // don't filter by row groups
                        null                                         // The sort order
                )
        ) {
            if (cursor == null || cursor.getCount() <= 0) {
                Log.d(TAG, "Method retrieveSmartphoneUse > CallsInfo: No calls found by query");
                return null;
            }

            // At most two rows allowed: Incoming and Outcoming
            if(cursor.getCount() > 2){
                Log.e(TAG, "Method retrieveSmartphoneUse > CallsInfo: Call query error");
                return null;
            }

            // Read Incoming Calls
            while (cursor.moveToNext()) {
                if(cursor.getInt(0) == INCOMING_TYPE){

                    smartphoneUse.setCallsIN(cursor.getInt(1));
                    smartphoneUse.setCallsPeopleIN(cursor.getInt(2));
                    smartphoneUse.setCallsDurationIN(cursor.getInt(3));

                } else if(cursor.getInt(0) == OUTCOMING_TYPE){

                    smartphoneUse.setCallsOUT(cursor.getInt(1));
                    smartphoneUse.setCallsPeopleOUT(cursor.getInt(2));
                    smartphoneUse.setCallsDurationOUT(cursor.getInt(3));

                } else{
                    Log.e(TAG, "Unexpected Call type: "+cursor.getInt(0));
                }
            }

            smartphoneUse.setCalls(smartphoneUse.getCallsIN() + smartphoneUse.getCallsOUT());
            smartphoneUse.setCallsDuration(smartphoneUse.getCallsDurationIN() + smartphoneUse.getCallsDurationOUT());
        }
        return smartphoneUse;
    }

    @Nullable
    private SmartphoneUse getSmartphonePeopleCallsInfo(SQLiteDatabase db,
                                                       SmartphoneUse smartphoneUse,
                                                       long dateMillis){

        if(smartphoneUse == null){
            smartphoneUse = new SmartphoneUse();
        }
        if(db == null){
            db = dbHelper.getReadableDatabase();
        }

        String[] projection = {
                "COUNT( distinct "+DatabaseContract.CallTable.NUMBER_COL4+") as CallPeople"
        };

        String selection = DatabaseContract.CallTable.DATE_COL1+
                " BETWEEN ? AND ?";

        String[] selectionArgs = {
                getSpecificDayMidnight(dateMillis),
                fromMillisToString(dateMillis)
        };

        try (
                Cursor cursor = db.query(
                        DatabaseContract.CallTable.TABLE_NAME,       // The table to query
                        projection,                                  // The columns to return
                        selection,                                   // The columns for the WHERE clause
                        selectionArgs,                               // The values for the WHERE clause
                        null,                                        // Group the rows
                        null,                                        // don't filter by row groups
                        null                                         // The sort order
                )
        ) {
            if (cursor == null || cursor.getCount() <= 0) {
                Log.d(TAG, "Method retrieveSmartphoneUse > PeopleCallsInfo: No calls found by query");
                return null;
            }

            cursor.moveToNext();
            smartphoneUse.setCallsPeople(cursor.getInt(0));

        }
        return smartphoneUse;
    }

    @Nullable
    private SmartphoneUse getSmartphonePeopleMessagesInfo(SQLiteDatabase db,
                                                          SmartphoneUse smartphoneUse,
                                                          long dateMillis){

        if(smartphoneUse == null){
            smartphoneUse = new SmartphoneUse();
        }
        if(db == null){
            db = dbHelper.getReadableDatabase();
        }

        String[] projection = {
                "COUNT( distinct "+DatabaseContract.MessageTable.NUMBER_COL3+") as MessagePeople"
        };

        String selection = DatabaseContract.MessageTable.DATE_COL1+
                " BETWEEN ? AND ?";

        String[] selectionArgs = {
                getSpecificDayMidnight(dateMillis),
                fromMillisToString(dateMillis)
        };

        try (
                Cursor cursor = db.query(
                        DatabaseContract.MessageTable.TABLE_NAME,    // The table to query
                        projection,                                  // The columns to return
                        selection,                                   // The columns for the WHERE clause
                        selectionArgs,                               // The values for the WHERE clause
                        null,                                        // Group the rows
                        null,                                        // don't filter by row groups
                        null                                         // The sort order
                )
        ) {
            if (cursor == null || cursor.getCount() <= 0) {
                Log.d(TAG, "Method retrieveSmartphoneUse > PeopleMessagesInfo: No messages found by query");
                return null;
            }

            cursor.moveToNext();
            smartphoneUse.setMessagesPeople(cursor.getInt(0));

        }
        return smartphoneUse;
    }


    /***** PEOPLE USE *****/
    public List<PeopleUseCall> retrievePeopleUseCall(long dateMillis){
        Log.v(TAG, "Method retrievePeopleUseCall: start");

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                DatabaseContract.CallTable.NUMBER_COL4,
                DatabaseContract.CallTable.TYPE_COL3,
                "COUNT(distinct "+DatabaseContract.CallTable._ID+") as TotalNumber",
                "SUM(distinct "+DatabaseContract.CallTable.DURATION_COL2+") as TotalDuration",

        };

        String selection = DatabaseContract.CallTable.DATE_COL1+
                " BETWEEN ? AND ?";

        String[] selectionArgs = {
                getSpecificDayMidnight(dateMillis),
                fromMillisToString(dateMillis)
        };

        String groupBy = DatabaseContract.CallTable.NUMBER_COL4+", "+DatabaseContract.CallTable.TYPE_COL3;

        List<PeopleUseCall> peopleUseList = new ArrayList<>();
        try (
                Cursor cursor = db.query(
                        DatabaseContract.CallTable.TABLE_NAME,       // The table to query
                        projection,                                  // The columns to return
                        selection,                                   // The columns for the WHERE clause
                        selectionArgs,                               // The values for the WHERE clause
                        groupBy,                                     // Group the rows
                        null,                                        // don't filter by row groups
                        null                                         // The sort order
                )
        ) {
            if (cursor == null || cursor.getCount() <= 0) {
                Log.d(TAG, "Method retrievePeopleUseCall: No calls found by query");
                return null;
            }

            // Read Incoming Calls
            while (cursor.moveToNext()) {
                PeopleUseCall puc = new PeopleUseCall("C",
                        cursor.getInt(0),
                        cursor.getInt(2),
                        cursor.getInt(1) == INCOMING_TYPE ? "IN" : "OUT",
                        cursor.getInt(3)
                );
                peopleUseList.add(puc);
            }
        }

        Log.v(TAG, "Method retrievePeopleUseCall: end");
        return peopleUseList;
    }

    public List<PeopleUseMessage> retrievePeopleUseMessage(long dateMillis){
        Log.v(TAG, "Method retrievePeopleUseMessage: start");

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                DatabaseContract.MessageTable.NUMBER_COL3,
                DatabaseContract.MessageTable.TYPE_COL2,
                "COUNT(distinct "+DatabaseContract.MessageTable._ID+") as TotalNumber"
        };

        String selection = DatabaseContract.MessageTable.DATE_COL1+
                " BETWEEN ? AND ?";

        String[] selectionArgs = {
                getSpecificDayMidnight(dateMillis),
                fromMillisToString(dateMillis)
        };

        String groupBy = DatabaseContract.MessageTable.NUMBER_COL3+", "+DatabaseContract.MessageTable.TYPE_COL2;

        List<PeopleUseMessage> peopleUseList = new ArrayList<>();
        try (
                Cursor cursor = db.query(
                        DatabaseContract.MessageTable.TABLE_NAME,    // The table to query
                        projection,                                  // The columns to return
                        selection,                                   // The columns for the WHERE clause
                        selectionArgs,                               // The values for the WHERE clause
                        groupBy,                                     // Group the rows
                        null,                                        // don't filter by row groups
                        null                                         // The sort order
                )
        ) {
            if (cursor == null || cursor.getCount() <= 0) {
                Log.d(TAG, "Method retrievePeopleUseMessage: No messages found by query");
                return null;
            }

            // Read Incoming Calls
            while (cursor.moveToNext()) {
                PeopleUseMessage pum = new PeopleUseMessage(
                        "M",
                        cursor.getInt(0),
                        cursor.getInt(2),
                        cursor.getInt(1) == INCOMING_TYPE ? "IN" : "OUT"
                );
                peopleUseList.add(pum);
            }
        }

        Log.v(TAG, "Method retrievePeopleUseMessage: end");
        return peopleUseList;
    }
}
