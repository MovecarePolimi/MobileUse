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

public class HttpLoginManager extends HttpAbstract{
    private static final String TAG = "HttpLoginManager";

    private final static int TIMEOUT_VALUE = 1000;
    private Context context;

    private final static String USERNAME_STRING = "username";
    private final static String PASSWORD_STRING = "password";
    private final static String APPLICATION_STRING = "application";
    private final static String GRANT_TYPE_STRING = "grant_type";
    private final static String TENANT_STRING = "tenant";

    /* Positive response code: JSON field names */
    private final static String ACCESS_TOKEN_STRING = "access_token";
    private final static String TOKEN_TYPE_STRING = "token_type";
    private final static String REFRESH_TOKEN_STRING = "refresh_token";
    private final static String EXPIRES_IN_STRING = "expires_in";
    private final static String JTI_STRING = "jti";


    private final static String[] loginParamsName = {
            GRANT_TYPE_STRING,
            USERNAME_STRING,
            PASSWORD_STRING,
            APPLICATION_STRING,
            TENANT_STRING
    };

    private final static String[] refreshTokenParamsName = {
            GRANT_TYPE_STRING,
            REFRESH_TOKEN_STRING,
            APPLICATION_STRING,
            TENANT_STRING
    };

    public enum MessageType{
        MSG_OK,
        MGS_NO_INTERNET,
        MSG_BAD_CREDENTIALS,
        MSG_ERR,
        MSG_INVALID_TOKEN,
        MSG_INVALID_REFRESH_TOKEN
    }

    private final static String LOGIN_POST_URL = "http://api.movecare-project.eu/oauth/token";
    private final static String GET_USERID_URL = "http://api.movecare-project.eu/movecare/datacenter/v1/userid";
    private final static String GET_ALL_USER_DATA_URL = "http://api.movecare-project.eu/usercontrol/data/v1/api/users/";

    public HttpLoginManager(Context context){
        this.context = context;
    }


    public void doLogin(String[] paramsList){
        Log.v(TAG,"Method DoLogin: start");
        if(!isConnected(context)){
            Log.i(TAG, "Internet not available");
            sendMessage(MessageType.MGS_NO_INTERNET);
            return;
        }

        try {
            // retrieveToken calls retrieveUserID, which in turn calls retrieveAllUserData
            retrieveToken(paramsList);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            sendMessage(MessageType.MSG_ERR);
        }
    }

    public void doRefreshToken(String[] paramsList){
        if(!isConnected(context)){
            Log.i(TAG, "Internet not available");
            sendMessage(MessageType.MGS_NO_INTERNET);
            return;
        }

        try {
            refreshToken(paramsList);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            sendMessage(MessageType.MSG_ERR);
        }
    }


    private void retrieveToken(String[] paramsList) throws IOException {

        URL url = new URL(LOGIN_POST_URL);
        InputStream stream = null;

        // Future work: switch to HTTPS (when Eurecat will change it)
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(TIMEOUT_VALUE);
            connection.setConnectTimeout(TIMEOUT_VALUE);
            connection.setRequestMethod("POST");

            connection.setDoInput(true);
            connection.setDoOutput(true);

            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // Open communications link (network traffic occurs here).
            connection.connect();
            DataOutputStream os = new DataOutputStream(connection.getOutputStream());
            os.writeBytes(getParams(loginParamsName, paramsList));
            int responseCode = connection.getResponseCode();
            Log.v(TAG, "Sending Login Request..");

            // BAD CREDENTIALS
            if(responseCode == HttpsURLConnection.HTTP_INTERNAL_ERROR){
                Log.e(TAG, "Wrong credentials");
                sendMessage(MessageType.MSG_BAD_CREDENTIALS);
                return;
            }

            // OTHER ERROR TYPE
            if(responseCode != HttpsURLConnection.HTTP_OK){
                Log.e(TAG, "Other error");
                sendMessage(MessageType.MSG_ERR);
                return;
            }

            // Get the response body as an InputStream.
            stream = connection.getInputStream();
            if (stream != null) {
                String responseText = getResponseText(stream);

                JSONObject json;
                String strAccessToken, strTokenType, strRefreshToken, strExpiresIn, strJti;
                try {
                    // Convert String to json object
                    json = new JSONObject(responseText);
                    strAccessToken = json.getString(ACCESS_TOKEN_STRING);
                    strTokenType = json.getString(TOKEN_TYPE_STRING);
                    strRefreshToken = json.getString(REFRESH_TOKEN_STRING);
                    strExpiresIn = json.getString(EXPIRES_IN_STRING);
                    strJti = json.getString(JTI_STRING);
                    Log.v(TAG, "JSON read");

                    // Store token data within shared preferences
                    LoginPreferences logPref = new LoginPreferences();

                    logPref.storeVariable(context, LoginPreferences.Variable.ACCESS_TOKEN, strAccessToken);
                    logPref.storeVariable(context, LoginPreferences.Variable.TOKEN_TYPE, strTokenType);
                    logPref.storeVariable(context, LoginPreferences.Variable.REFRESH_TOKEN, strRefreshToken);
                    logPref.storeVariable(context, LoginPreferences.Variable.EXPIRES_IN, strExpiresIn);
                    logPref.storeVariable(context, LoginPreferences.Variable.JTI, strJti);

                    retrieveUserID();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else{
                throw new IOException("Response stream: null");
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
            connection.setRequestMethod("GET");

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
                sendMessage(MessageType.MSG_INVALID_TOKEN);
                return;
            }

            // OTHER ERROR TYPE
            if(responseCode != HttpsURLConnection.HTTP_OK){
                Log.e(TAG, "Other error");
                sendMessage(MessageType.MSG_ERR);
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
            connection.setRequestMethod("GET");

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
                sendMessage(MessageType.MSG_INVALID_TOKEN);
                return;
            }

            // OTHER ERROR TYPE
            if(responseCode != HttpsURLConnection.HTTP_OK){
                Log.e(TAG, "Other error");
                sendMessage(MessageType.MSG_ERR);
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

                    sendMessage(MessageType.MSG_OK);
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
    
    private void refreshToken(String[] paramsList) throws IOException {

        URL url = new URL(LOGIN_POST_URL);
        InputStream stream = null;

        // Future work: switch to HTTPS (when Eurecat will change it)
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(1000);
            connection.setConnectTimeout(1000);
            connection.setRequestMethod("POST");

            connection.setDoInput(true);
            connection.setDoOutput(true);

            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // Open communications link (network traffic occurs here).
            connection.connect();

            DataOutputStream os = new DataOutputStream(connection.getOutputStream());
            os.writeBytes(getParams(refreshTokenParamsName, paramsList));

            int responseCode = connection.getResponseCode();
            Log.v(TAG, "Sending Login Request..");

            if(responseCode != HttpsURLConnection.HTTP_OK){
                Log.e(TAG, "Refresh token request error: do login again");
                sendMessage(MessageType.MSG_INVALID_REFRESH_TOKEN);
                return;
            }

            // Get the response body as an InputStream.
            stream = connection.getInputStream();
            if (stream != null) {
                String responseText = getResponseText(stream);

                JSONObject json;
                String strAccessToken, strTokenType, strRefreshToken, strExpiresIn, strJti;
                try {
                    // Convert String to json object
                    json = new JSONObject(responseText);
                    strAccessToken = json.getString(ACCESS_TOKEN_STRING);
                    strTokenType = json.getString(TOKEN_TYPE_STRING);
                    strRefreshToken = json.getString(REFRESH_TOKEN_STRING);
                    strExpiresIn = json.getString(EXPIRES_IN_STRING);
                    strJti = json.getString(JTI_STRING);
                    Log.v(TAG, "JSON read");

                    // Store token data within shared preferences
                    LoginPreferences logPref = new LoginPreferences();

                    logPref.storeVariable(context, LoginPreferences.Variable.ACCESS_TOKEN, strAccessToken);
                    logPref.storeVariable(context, LoginPreferences.Variable.TOKEN_TYPE, strTokenType);
                    logPref.storeVariable(context, LoginPreferences.Variable.REFRESH_TOKEN, strRefreshToken);
                    logPref.storeVariable(context, LoginPreferences.Variable.EXPIRES_IN, strExpiresIn);
                    logPref.storeVariable(context, LoginPreferences.Variable.JTI, strJti);
                    Log.v(TAG, "Token stored in Shared Preferences");

                    retrieveUserID();

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

    private String getParams(String[] paramsName, String[] paramsValue) throws UnsupportedEncodingException
    {
        if(paramsName == null || paramsValue == null || (paramsName.length != paramsValue.length)){
            Log.e(TAG, "Parameters error: error on name or value");
            return null;
        }

        StringBuilder result = new StringBuilder();
        boolean first = true;

        for(int i = 0; i< paramsName.length; i++){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(paramsName[i], "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(paramsValue[i], "UTF-8"));
        }

        return result.toString();
    }

    private void sendMessage(MessageType msg) {
        Log.v(TAG, "Send broadcasting message");
        Intent intent = new Intent("custom-event-name");

        intent.putExtra("message", msg);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    private String getResponseText(InputStream stream) throws IOException {

        if(stream == null){
            Log.e(TAG, "Method GetResponseString: received NULL stream object");
            throw new IOException();
        }

        StringBuilder result = new StringBuilder();

        Reader reader = new InputStreamReader(stream, "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(reader);

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            result.append(line);
        }

        return result.toString();
    }

}
