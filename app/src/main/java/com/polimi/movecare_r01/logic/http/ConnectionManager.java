package com.polimi.movecare_r01.logic.http;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.polimi.movecare_r01.logic.exceptions.InternetConnectionException;
import com.polimi.movecare_r01.logic.observers.JsonSentObserver;
import com.polimi.movecare_r01.logic.observers.JsonSentSubject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class ConnectionManager extends HttpAbstract{

    private static final String TAG = ConnectionManager.class.getSimpleName();

    private final static String HTTP                = "http://";
    private final static String SERVER_IP           = "35.158.208.168";
    private final static String PORT                = ":10011";
    private final static String WS_NAME             = "/smartphone";
    private final static String MESS                = "/message";
    private final static String CALL                = "/call";
    private final static String SMARTPHONE_USE      = "/smartphoneuse";
    private final static String PEOPLE_USE          = "/peopleuse";
    private final static String POST_MSG_URL        = HTTP+ SERVER_IP + PORT + WS_NAME + MESS;
    private final static String POST_CALL_URL       = HTTP+ SERVER_IP + PORT + WS_NAME + CALL;
    private final static String POST_SMARTPHONE_URL = HTTP+ SERVER_IP + PORT + WS_NAME + SMARTPHONE_USE;
    private final static String POST_PEOPLE_URL     = HTTP+ SERVER_IP + PORT + WS_NAME + PEOPLE_USE;

    private final static String JSON_OK             = "{\"success\":true}";

    private final static String[] reportArray = {"SUM", "SUC", "SSU", "PSU"};
    private static final Intent[] JSON_INTENT = {
            new Intent("JSON_SUM_SENT"),
            new Intent("JSON_SUC_SENT"),
            new Intent("JSON_SSU_SENT"),
            new Intent("JSON_PSU_SENT")
    };

    private Context context;
    private JsonSentSubject subject;
    private JsonSentObserver observer;

    public ConnectionManager(Context context){
        this.context = context;
        this.subject = new JsonSentSubject();
        this.observer = new JsonSentObserver(context, "123456789", subject);
    }


    public void sendJSON(String msgJSON,
                              String callJSON,
                              String smartphoneUseJSON,
                              String peopleUseJSON,
                              String timeMillis)
            throws IOException, InternetConnectionException {

        if(!isConnected(context)){

            throw  new InternetConnectionException();
        }

        if(getConnectionType(context) != ConnectionType.WIFI){
            Log.w(TAG, "Warning: No Wi-Fi connection");
        }


        sendMessageJSON(msgJSON, timeMillis);
        sendCallJSON(callJSON, timeMillis);
        sendSmartphoneUseJSON(smartphoneUseJSON, timeMillis);
        sendPeopleUseJSON(peopleUseJSON, timeMillis);
    }

    private void sendMessageJSON(String msgJSON, String timeMillis) throws IOException {

        URL url = new URL(POST_MSG_URL);

        InputStream stream = null;
        // Future work: switch to HTTPS (when Eurecat will change it)
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(3000);
            connection.setConnectTimeout(3000);
            connection.setRequestMethod("POST");

            connection.setDoInput(true);
            connection.setDoOutput(true);

            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json");

            // Open communications link (network traffic occurs here).
            connection.connect();

            DataOutputStream os = new DataOutputStream(connection.getOutputStream());
            os.writeBytes(msgJSON);

            int responseCode = connection.getResponseCode();
            Log.i(TAG, "Sending SUM json..");
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }

            // Retrieve the response body as an InputStream.
            stream = connection.getInputStream();

            if (stream != null && checkServerResponse(stream)) {
                // send result to JsonSentObserver
                subject.setState(true, timeMillis);
            } else{
                throw new IOException("Response json: null");
            }
        }
        finally {
            // Close Stream and disconnect HTTPS connection.
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private void sendCallJSON(String callJSON, String timeMillis) throws IOException {

        URL url = new URL(POST_CALL_URL);

        InputStream stream = null;
        // Future work: switch to HTTPS (when Eurecat will change it)
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(3000);
            connection.setConnectTimeout(3000);
            connection.setRequestMethod("POST");

            connection.setDoInput(true);
            connection.setDoOutput(true);

            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json");

            // Open communications link (network traffic occurs here).
            connection.connect();

            DataOutputStream os = new DataOutputStream(connection.getOutputStream());
            os.writeBytes(callJSON);

            int responseCode = connection.getResponseCode();
            Log.e(TAG, "Sending SUC json..");
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }
            // Retrieve the response body as an InputStream.
            stream = connection.getInputStream();

            if (stream != null && checkServerResponse(stream)) {
                // send result to JsonSentObserver
                subject.setState(true, timeMillis);
            } else{
                throw new IOException("Response json: null");
            }
        }
        finally {
            // Close Stream and disconnect HTTPS connection.
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private void sendSmartphoneUseJSON(String smartphoneUseJSON, String timeMillis) throws IOException {

        URL url = new URL(POST_SMARTPHONE_URL);

        InputStream stream = null;
        // Future work: switch to HTTPS (when Eurecat will change it)
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(3000);
            connection.setConnectTimeout(3000);
            connection.setRequestMethod("POST");

            connection.setDoInput(true);
            connection.setDoOutput(true);

            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json");

            // Open communications link (network traffic occurs here).
            connection.connect();

            DataOutputStream os = new DataOutputStream(connection.getOutputStream());
            os.writeBytes(smartphoneUseJSON);

            int responseCode = connection.getResponseCode();
            Log.e(TAG, "Sending SSU json..");
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }

            // Retrieve the response body as an InputStream.
            stream = connection.getInputStream();

            if (stream != null && checkServerResponse(stream)) {
                // send result to JsonSentObserver
                subject.setState(true, timeMillis);
            } else{
                throw new IOException("Response json: null");
            }
        }
        finally {
            // Close Stream and disconnect HTTPS connection.
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private void sendPeopleUseJSON(String peopleUseJSON, String timeMillis) throws IOException {
        URL url = new URL(POST_PEOPLE_URL);

        InputStream stream = null;
        // Future work: switch to HTTPS (when Eurecat will change it)
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(3000);
            connection.setConnectTimeout(3000);
            connection.setRequestMethod("POST");

            connection.setDoInput(true);
            connection.setDoOutput(true);

            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json");

            // Open communications link (network traffic occurs here).
            connection.connect();

            DataOutputStream os = new DataOutputStream(connection.getOutputStream());
            os.writeBytes(peopleUseJSON);

            int responseCode = connection.getResponseCode();
            Log.e(TAG, "Sending PSU json..");
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }

            // Retrieve the response body as an InputStream.
            stream = connection.getInputStream();

            if (stream != null && checkServerResponse(stream)) {
                // send result to JsonSentObserver
                subject.setState(true, timeMillis);
            } else{
                throw new IOException("Response json: null");
            }
        }
        finally {
            // Close Stream and disconnect HTTPS connection.
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }


    private boolean checkServerResponse(InputStream stream) throws IOException {

        int maxReadSize = 16;
        Reader reader;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] rawBuffer = new char[maxReadSize];
        int readSize;
        StringBuilder buffer = new StringBuilder();
        while (((readSize = reader.read(rawBuffer)) != -1) && maxReadSize > 0) {
            if (readSize > maxReadSize) {
                readSize = maxReadSize;
            }
            buffer.append(rawBuffer, 0, readSize);
            maxReadSize -= readSize;
        }
        String responseValue = buffer.toString();

        if(responseValue.equals(JSON_OK)){
            Log.e("----", "Everything is fine");
            return true;
        } else{
            Log.e("----", "Response not expected");
            return false;
        }
    }

}
