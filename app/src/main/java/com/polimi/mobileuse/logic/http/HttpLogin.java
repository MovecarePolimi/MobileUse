package com.polimi.mobileuse.logic.http;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.polimi.mobileuse.dao.preferences.LoginPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

public class HttpLogin extends HttpAbstract{
    private static final String TAG = "HttpLogin";

    public enum MessageType{
        MSG_OK,
        MGS_NO_INTERNET,
        MSG_BAD_CREDENTIALS,
        MSG_ERR,
        MSG_INVALID_TOKEN,
        MSG_INVALID_REFRESH_TOKEN
    }

    private final static String GET_USERID_URL = "http://api.movecare-project.eu/movecare/datacenter/v1/userid";
    private final static String GET_ALL_USER_DATA_URL = "http://api.movecare-project.eu/usercontrol/data/v1/api/users/";

    public HttpLogin(Context context, String broadcastEventName){
        this.context = context;
        this.broadcastEventName = broadcastEventName;
    }


    public void doLogin(){
        Log.v(TAG,"Method DoLogin: start");
        if(!isConnected(context)){
            Log.i(TAG, "Internet not available");
            sendMessage(broadcastEventName, MessageType.MGS_NO_INTERNET);
            return;
        }

        try {
            // retrieveUserID, which in turn calls retrieveAllUserData
            retrieveUserID();
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            sendMessage(broadcastEventName, MessageType.MSG_ERR);
        }
    }


    private void retrieveUserID() throws IOException {
        // Possible only if token is stored --> get token
        LoginPreferences logPrefs = new LoginPreferences();
        String accessToken = logPrefs.getVariable(context, LoginPreferences.Variable.ACCESS_TOKEN);
        if(accessToken == null){
            Log.e(TAG, "Token not available");
            return;
        }

        URL url = new URL(GET_USERID_URL);

        InputStream stream = null;

        // Future work: switch to HTTPS (when Eurecat will change it)
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(3000);
            connection.setConnectTimeout(3000);
            connection.setRequestMethod(GET_STRING);

            connection.setDoInput(true);

            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer "+accessToken);

            // Open communications link (network traffic occurs here).
            connection.connect();

            int responseCode = connection.getResponseCode();
            Log.v(TAG, "Sending GET_USER_ID Request..");

            // INVALID ACCESS_TOKEN
            if(responseCode == HttpsURLConnection.HTTP_UNAUTHORIZED){
                Log.e(TAG, "Token not valid");
                sendMessage(broadcastEventName, MessageType.MSG_INVALID_TOKEN);
                return;
            }

            // OTHER ERROR TYPE
            if(responseCode != HttpsURLConnection.HTTP_OK){
                Log.e(TAG, "Other error");
                sendMessage(broadcastEventName, MessageType.MSG_ERR);
                return;
            }

            // Retrieve the response body as an InputStream.
            stream = connection.getInputStream();

            if (stream != null) {
                // store token in SharedPreferences
                Log.v(TAG,"Data received. Validating ...");

                StringBuilder result = new StringBuilder();

                Reader reader = new InputStreamReader(stream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(reader);

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line);
                }

                String responseText = result.toString();

                JSONObject json;
                String strUserID;
                try {
                    // Convert String to json object
                    json = new JSONObject(responseText);
                    strUserID = json.getString("userid");
                    Log.v(TAG, "JSON read");

                    // Store token data within shared preferences
                    logPrefs.storeVariable(context, LoginPreferences.Variable.UUID, strUserID);
                    Log.v(TAG, "UUID stored in Shared Preferences");

                    retrieveAllUserData();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

    private void retrieveAllUserData() throws IOException {

        LoginPreferences logPrefs = new LoginPreferences();
        String userId = logPrefs.getVariable(context, LoginPreferences.Variable.UUID);
        String accessToken = logPrefs.getVariable(context, LoginPreferences.Variable.ACCESS_TOKEN);
        if(userId == null || accessToken == null){
            Log.e(TAG, "UserID or AccessToken not available");
            return;
        }

        URL url = new URL(GET_ALL_USER_DATA_URL+userId);

        InputStream stream = null;
        // Future work: switch to HTTPS (when Eurecat will change it)
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(3000);
            connection.setConnectTimeout(3000);
            connection.setRequestMethod(GET_STRING);

            connection.setDoInput(true);

            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer "+accessToken);

            // Open communications link (network traffic occurs here).
            connection.connect();

            int responseCode = connection.getResponseCode();
            Log.v(TAG, "Sending GET_ALL_USER_DATA Request..");

            // INVALID ACCESS_TOKEN
            if(responseCode == HttpsURLConnection.HTTP_UNAUTHORIZED){
                Log.e(TAG, "Token not valid");
                sendMessage(broadcastEventName, MessageType.MSG_INVALID_TOKEN);
                return;
            }

            // OTHER ERROR TYPE
            if(responseCode != HttpsURLConnection.HTTP_OK){
                Log.e(TAG, "Other error");
                sendMessage(broadcastEventName, MessageType.MSG_ERR);
                return;
            }

            // Retrieve the response body as an InputStream.
            stream = connection.getInputStream();

            if (stream != null) {
                // store token in SharedPreferences
                Log.v(TAG,"Data received. Validating ...");

                StringBuilder result = new StringBuilder();

                Reader reader = new InputStreamReader(stream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(reader);

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line);
                }

                String responseText = result.toString();

                JSONObject json;
                String strUsername, strEmail;
                try {
                    // Convert String to json object
                    json = new JSONObject(responseText);
                    strUsername = json.getString("username");
                    strEmail = json.getString("email");
                    Log.v(TAG, "JSON read");

                    // Store token data within shared preferences
                    //LoginPreferences logPref = new LoginPreferences();
                    logPrefs.storeVariable(context, LoginPreferences.Variable.USERNAME, strUsername);
                    logPrefs.storeVariable(context, LoginPreferences.Variable.EMAIL, strEmail);
                    Log.v(TAG, "User data stored in Shared Preferences");

                    sendMessage(broadcastEventName, MessageType.MSG_OK);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
    


}
