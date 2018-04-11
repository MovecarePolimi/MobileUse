package com.polimi.mobileuse.model.phoneReport;


import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public abstract class DateConverter {
    private static final String TAG = DateConverter.class.getSimpleName();

    protected Calendar date;

    private final static String INSOLES_DATE_FORMAT = "EEE, dd MMM yyyy - HH:mm:ss";

    protected DateFormat dateFormat = DateFormat.getDateInstance();
    protected DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);

    protected Calendar fromMillisToCalendar(long millis){

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);

        return calendar;
    }

    /*protected Date fromStringToDate(String d){
        try {
            return timeFormat.parse(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }*/


    /* Different date getter methods */
    public Calendar getDate() {
        return date;
    }

    public long getDateMillis() {
        return date.getTimeInMillis();
    }

    public String getDateString(){
        return dateFormat.format(date.getTime());
    }

    /* Different date setter methods */
    public void setDate(Calendar date) {
        this.date = date;
    }

    public void setDate(String date) {
        this.date = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(INSOLES_DATE_FORMAT, Locale.ENGLISH);
        try {
            this.date.setTime(sdf.parse("Tue, 26 Sep 2017 - 09:36:10"));// --> Devo avere il mese a 3 lettere
        } catch (ParseException e) {
            Log.e(TAG, "ParseException: cannot parse date, format not valid \n"+e);
            this.date = null;
        }
    }

    public void setDateMillis(long dateMillis) {
        this.date = fromMillisToCalendar(dateMillis);
    }

    public String getTimeString() {
        return timeFormat.format(date.getTime());
    }
}
