package com.polimi.mobileuse.logic.http;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.polimi.mobileuse.applicationLogic.service.RefreshTokenService;
import com.polimi.mobileuse.dao.preferences.LoginPreferences;
import com.polimi.mobileuse.logic.exceptions.InternetConnectionException;
import com.polimi.mobileuse.logic.observers.JsonSentObserver;
import com.polimi.mobileuse.logic.observers.JsonSentSubject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class HttpStoreData extends HttpAbstract{

    private static final String TAG = HttpStoreData.class.getSimpleName();

    private final static String BROADCAST_EVENT_NAME    = "store_event";

    private final static String POST_MSG_URL        = "http://api.movecare-project.eu/movecare/smartphone/message";
    private final static String POST_CALL_URL       = "http://api.movecare-project.eu/movecare/smartphone/call";
    private final static String POST_SMARTPHONE_URL = "http://api.movecare-project.eu/movecare/smartphone/smartphoneuse";
    private final static String POST_PEOPLE_URL     = "http://api.movecare-project.eu/movecare/smartphone/peopleuse";

    private final static String TENANT_UUID_STRING  = "tenant-uuid";
    private final static String APPLICATION_UUID_STRING = "application-uuid";
    private final static String AUTHORIZATION_STRING    = "Authorization";
    private final static String CONTENT_TYPE_STRING     = "Content-Type";

    private final static String TENANT_UUID         = "XXXXX";
    private final static String APPLICATION_UUID    = "XXXXXXX";
    private final static String CONTENT_TYPE        = "application/json";



    private final static String SUCCESS_STRING      = "success";
    private final static String ERROR_STRING        = "error";
    private final static String JSON_OK             = "{\"success\":true}";

    private static final Intent[] JSON_INTENT = {
            new Intent("JSON_SUM_SENT"),
            new Intent("JSON_SUC_SENT"),
            new Intent("JSON_SSU_SENT"),
            new Intent("JSON_PSU_SENT")
    };

    private Context context;
    private JsonSentSubject subject;
    private JsonSentObserver observer;

    private String accessToken;

    public HttpStoreData(Context context){
        this.context = context;
        this.subject = new JsonSentSubject();
        this.observer = new JsonSentObserver(context, subject);
    }



    public void sendJSON(String msgJSON,
                              String callJSON,
                              String smartphoneUseJSON,
                              String peopleUseJSON,
                              String timeMillis)
            throws Exception {

        if(!isConnected(context)){
            Log.i(TAG, "Internet not available");
            throw  new InternetConnectionException();
        }

        TokenManager tokenManager = new TokenManager(context, null);
        tokenManager.refreshToken();

        LoginPreferences logsPreferences = new LoginPreferences();
        accessToken = logsPreferences.getVariable(context, LoginPreferences.Variable.ACCESS_TOKEN);

        sendMessageJSON(msgJSON, timeMillis);
        sendCallJSON(callJSON, timeMillis);
        sendSmartphoneUseJSON(smartphoneUseJSON, timeMillis);
        sendPeopleUseJSON(peopleUseJSON, timeMillis);

    }

    private void sendMessageJSON(String msgJSON, String timeMillis) throws Exception {

        URL url = new URL(POST_MSG_URL);
        InputStream stream = null;

        // Future work: switch to HTTPS (when Eurecat will change it)
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(TIMEOUT_VALUE);
            connection.setConnectTimeout(TIMEOUT_VALUE);
            connection.setRequestMethod(POST_STRING);

            connection.setDoInput(true);
            connection.setDoOutput(true);

            connection.setRequestProperty(CONTENT_TYPE_STRING, CONTENT_TYPE);
            connection.setRequestProperty(AUTHORIZATION_STRING, "Bearer "+accessToken);
            connection.setRequestProperty(TENANT_UUID_STRING, TENANT_UUID);
            connection.setRequestProperty(APPLICATION_UUID_STRING, APPLICATION_UUID);

            // Open communications link (network traffic occurs here).
            connection.connect();
            DataOutputStream os = new DataOutputStream(connection.getOutputStream());
            os.writeBytes(msgJSON);
            int responseCode = connection.getResponseCode();
            Log.v(TAG, "Sending Message Post Request..");

            // OTHER ERROR TYPE
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                Log.e(TAG, "Other error");
                sendMessage(BROADCAST_EVENT_NAME, HttpLogin.MessageType.MSG_ERR);
                return;
            }

            // Get the response body as an InputStream.
            stream = connection.getInputStream();
            if (stream != null /*&& checkServerResponse(stream)*/) {
                // send result to JsonSentObserver
                String responseText = getResponseText(stream);

                JSONObject json;
                boolean success;
                String strError;

                // Convert String to json object
                json = new JSONObject(responseText);
                success = json.getBoolean(SUCCESS_STRING);
                if (!success) {
                    strError = json.getString(ERROR_STRING);
                    Log.e(TAG, strError);
                }
                Log.v(TAG, "JSON read");

                subject.setState(true, timeMillis);
            } else{
                throw new IOException("Response stream: null");
            }
        } finally {
            // Close Stream and disconnect HTTPS connection.
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private void sendCallJSON(String callJSON, String timeMillis) throws Exception {

        URL url = new URL(POST_CALL_URL);
        InputStream stream = null;

        // Future work: switch to HTTPS (when Eurecat will change it)
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(TIMEOUT_VALUE);
            connection.setConnectTimeout(TIMEOUT_VALUE);
            connection.setRequestMethod(POST_STRING);

            connection.setDoInput(true);
            connection.setDoOutput(true);

            connection.setRequestProperty(CONTENT_TYPE_STRING, CONTENT_TYPE);
            connection.setRequestProperty(AUTHORIZATION_STRING, "Bearer "+accessToken);
            connection.setRequestProperty(TENANT_UUID_STRING, TENANT_UUID);
            connection.setRequestProperty(APPLICATION_UUID_STRING, APPLICATION_UUID);

            // Open communications link (network traffic occurs here).
            connection.connect();
            DataOutputStream os = new DataOutputStream(connection.getOutputStream());
            os.writeBytes(callJSON);
            int responseCode = connection.getResponseCode();
            Log.v(TAG, "Sending Message Post Request..");

            // OTHER ERROR TYPE
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                Log.e(TAG, "Other error");
                sendMessage(BROADCAST_EVENT_NAME, HttpLogin.MessageType.MSG_ERR);
                return;
            }

            // Get the response body as an InputStream.
            stream = connection.getInputStream();
            if (stream != null /*&& checkServerResponse(stream)*/) {
                // send result to JsonSentObserver
                String responseText = getResponseText(stream);

                JSONObject json;
                boolean success;
                String strError;

                // Convert String to json object
                json = new JSONObject(responseText);
                success = json.getBoolean(SUCCESS_STRING);
                if (!success) {
                    strError = json.getString(ERROR_STRING);
                    Log.e(TAG, strError);
                }
                Log.v(TAG, "JSON read");

                subject.setState(true, timeMillis);
            } else{
                throw new IOException("Response stream: null");
            }
        } finally {
            // Close Stream and disconnect HTTPS connection.
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private void sendSmartphoneUseJSON(String smartphoneUseJSON, String timeMillis) throws Exception {

        URL url = new URL(POST_SMARTPHONE_URL);
        InputStream stream = null;

        // Future work: switch to HTTPS (when Eurecat will change it)
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(TIMEOUT_VALUE);
            connection.setConnectTimeout(TIMEOUT_VALUE);
            connection.setRequestMethod(POST_STRING);

            connection.setDoInput(true);
            connection.setDoOutput(true);

            connection.setRequestProperty(CONTENT_TYPE_STRING, CONTENT_TYPE);
            connection.setRequestProperty(AUTHORIZATION_STRING, "Bearer "+accessToken);
            connection.setRequestProperty(TENANT_UUID_STRING, TENANT_UUID);
            connection.setRequestProperty(APPLICATION_UUID_STRING, APPLICATION_UUID);

            // Open communications link (network traffic occurs here).
            connection.connect();
            DataOutputStream os = new DataOutputStream(connection.getOutputStream());
            os.writeBytes(smartphoneUseJSON);
            int responseCode = connection.getResponseCode();
            Log.v(TAG, "Sending Message Post Request..");

            // OTHER ERROR TYPE
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                Log.e(TAG, "Other error");
                sendMessage(BROADCAST_EVENT_NAME, HttpLogin.MessageType.MSG_ERR);
                return;
            }

            // Get the response body as an InputStream.
            stream = connection.getInputStream();
            if (stream != null /*&& checkServerResponse(stream)*/) {
                // send result to JsonSentObserver
                String responseText = getResponseText(stream);

                JSONObject json;
                boolean success;
                String strError;

                // Convert String to json object
                json = new JSONObject(responseText);
                success = json.getBoolean(SUCCESS_STRING);
                if (!success) {
                    strError = json.getString(ERROR_STRING);
                    Log.e(TAG, strError);
                }
                Log.v(TAG, "JSON read");

                subject.setState(true, timeMillis);
            } else{
                throw new IOException("Response stream: null");
            }
        } finally {
            // Close Stream and disconnect HTTPS connection.
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private void sendPeopleUseJSON(String peopleUseJSON, String timeMillis) throws Exception {

        URL url = new URL(POST_PEOPLE_URL);
        InputStream stream = null;

        // Future work: switch to HTTPS (when Eurecat will change it)
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(TIMEOUT_VALUE);
            connection.setConnectTimeout(TIMEOUT_VALUE);
            connection.setRequestMethod(POST_STRING);

            connection.setDoInput(true);
            connection.setDoOutput(true);

            connection.setRequestProperty(CONTENT_TYPE_STRING, CONTENT_TYPE);
            connection.setRequestProperty(AUTHORIZATION_STRING, "Bearer "+accessToken);
            connection.setRequestProperty(TENANT_UUID_STRING, TENANT_UUID);
            connection.setRequestProperty(APPLICATION_UUID_STRING, APPLICATION_UUID);

            // Open communications link (network traffic occurs here).
            connection.connect();
            DataOutputStream os = new DataOutputStream(connection.getOutputStream());
            os.writeBytes(peopleUseJSON);
            int responseCode = connection.getResponseCode();
            Log.v(TAG, "Sending Message Post Request..");

            // OTHER ERROR TYPE
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                Log.e(TAG, "Other error");
                sendMessage(BROADCAST_EVENT_NAME, HttpLogin.MessageType.MSG_ERR);
                return;
            }

            // Get the response body as an InputStream.
            stream = connection.getInputStream();
            if (stream != null /*&& checkServerResponse(stream)*/) {
                // send result to JsonSentObserver
                String responseText = getResponseText(stream);

                JSONObject json;
                boolean success;
                String strError;

                // Convert String to json object
                json = new JSONObject(responseText);
                success = json.getBoolean(SUCCESS_STRING);
                if (!success) {
                    strError = json.getString(ERROR_STRING);
                    Log.e(TAG, strError);
                }
                Log.v(TAG, "JSON read");

                subject.setState(true, timeMillis);
            } else{
                throw new IOException("Response stream: null");
            }
        } finally {
            // Close Stream and disconnect HTTPS connection.
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
