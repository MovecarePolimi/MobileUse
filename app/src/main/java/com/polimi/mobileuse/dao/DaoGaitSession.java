package com.polimi.mobileuse.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.polimi.mobileuse.model.insoles.InsolesHeader;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DaoGaitSession extends Dao {
    private static final String TAG = DaoGaitSession.class.getSimpleName();

    public DaoGaitSession(Context context) {
        super(context);
    }

    public void storeGaitSession(InsolesHeader data) throws Exception {
        Log.v(TAG, "Method saveGaitSession: start");

        if (data == null) {
            Log.d(TAG, "Method saveGaitSession: null Object received");
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.GaitSessionTable.DATE_COL1, Calendar.getInstance().getTimeInMillis());
        values.put(DatabaseContract.GaitSessionTable.SAMPLE_RATE_COL2, data.getSampleRate());
        values.put(DatabaseContract.GaitSessionTable.INS_LEFT_ID_COL3, data.getLeftID());
        values.put(DatabaseContract.GaitSessionTable.INS_LEFT_SENS_COL4, data.getLeftSensor());
        values.put(DatabaseContract.GaitSessionTable.INS_RIGHT_ID_COL5, data.getRightID());
        values.put(DatabaseContract.GaitSessionTable.INS_RIGHT_SENS_COL6, data.getRightSensor());

        long newRowId = db.insert(
                DatabaseContract.GaitSessionTable.TABLE_NAME,
                null,
                values);

        if (newRowId == -1) {
            throw new Exception("DBInsertException");
        }

        Log.v(TAG, "Method saveGaitSession: end");
    }

    public List<InsolesHeader> retrieveGaitSessionByDay(long dateMillis){
        Log.v(TAG, "Method retrieveGaitSessionByDay: start");
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        /*String selection = "date BETWEEN ? AND ?";

        String[] selectionArgs = {
                getSpecificDayMidnight(dateMillis),
                fromMillisToString(dateMillis)
        };*/

        List<InsolesHeader> gaitSessionSet = new ArrayList<>();

        try (
                Cursor cursor = db.query(
                        DatabaseContract.GaitSessionTable.TABLE_NAME,       // The table to query
                        null,                                               // The columns to return
                        null,//selection,                                          // The columns for the WHERE clause
                        null,//selectionArgs,                                      // The values for the WHERE clause
                        null,                                               // don't group the rows
                        null,                                               // don't filter by row groups
                        null                                                // The sort order
                )
        ) {
            if (cursor == null || cursor.getCount() <= 0) {
                Log.d(TAG, "Method retrieveGaitSessionByDay: No gait_session found by query");
                return null;
            }

            while (cursor.moveToNext()) {
                InsolesHeader i = new InsolesHeader(
                        cursor.getDouble(2),     // sample rate
                        cursor.getInt(3),        // left insole id
                        cursor.getInt(4),        // left insole sensor
                        cursor.getInt(5),        // right insole id
                        cursor.getInt(6)         // right insole sensor
                );

                gaitSessionSet.add(i);
            }
        }

        Log.v(TAG, "Method retrieveGaitSessionByDay: end");
        return gaitSessionSet;
    }
}
