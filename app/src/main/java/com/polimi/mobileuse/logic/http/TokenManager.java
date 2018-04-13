package com.polimi.mobileuse.logic.http;

import android.content.Context;
import android.util.Log;

import com.polimi.mobileuse.dao.preferences.LoginPreferences;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class TokenManager extends HttpAbstract{
    private static final String TAG = "TokenManager";

    private final static String LOGIN_POST_URL = "http://api.movecare-project.eu/oauth/token";

    private static final String GRANT_TYPE_PW = "password";
    private static final String GRANT_TYPE_TOKEN = "refresh_token";
    // probably these two should be changed by EURECAT
    private static final String APPLICATION = "4bac09a5b8dd11e781af0242ac120002";
    private static final String TENANT = "5a258444b8dd11e781af0242ac120002";

    private Context context;

    public TokenManager(Context context, String broadcasterName){
        this.context = context;
        this.broadcastEventName = broadcasterName;
    }

    public void refreshToken(){
        if(!isConnected(context)){
            Log.i(TAG, "Internet not available");
            sendMessage(broadcastEventName, HttpLogin.MessageType.MGS_NO_INTERNET);
            return;
        }

        try {
            doRefreshToken();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            sendMessage(broadcastEventName, HttpLogin.MessageType.MSG_ERR);
        }
    }

    public boolean retrieveToken(String username, String password){
        Log.v(TAG,"Method DoLogin: start");
        if(!isConnected(context)){
            Log.i(TAG, "Internet not available");
            sendMessage(broadcastEventName, HttpLogin.MessageType.MGS_NO_INTERNET);
            return false;
        }

        try {
            // retrieveToken calls retrieveUserID, which in turn calls retrieveAllUserData
            return doRetrieveToken(username, password);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            sendMessage(broadcastEventName, HttpLogin.MessageType.MSG_ERR);
            return false;
        }
    }


    private void doRefreshToken() throws Exception {

        LoginPreferences logsPreferences = new LoginPreferences();
        final String REFRESH_TOKEN = logsPreferences.getVariable(context, LoginPreferences.Variable.REFRESH_TOKEN);

        final String[] refreshTokenParamsValue = {
                GRANT_TYPE_TOKEN,
                REFRESH_TOKEN,
                APPLICATION,
                TENANT
        };

        URL url = new URL(LOGIN_POST_URL);
        InputStream stream = null;

        // Future work: switch to HTTPS (when Eurecat will change it)
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(1000);
            connection.setConnectTimeout(1000);
            connection.setRequestMethod(POST_STRING);

            connection.setDoInput(true);
            connection.setDoOutput(true);

            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // Open communications link (network traffic occurs here).
            connection.connect();

            DataOutputStream os = new DataOutputStream(connection.getOutputStream());
            os.writeBytes(getParams(refreshTokenParamsName, refreshTokenParamsValue));

            int responseCode = connection.getResponseCode();
            Log.v(TAG, "Sending Refresh Token Request..");

            if(responseCode != HttpsURLConnection.HTTP_OK){
                Log.e(TAG, "Refresh token request error: do login again");
                sendMessage(broadcastEventName, HttpLogin.MessageType.MSG_INVALID_REFRESH_TOKEN);
                return;
            }

            // Get the response body as an InputStream.
            stream = connection.getInputStream();
            if (stream != null) {
                String responseText = getResponseText(stream);

                JSONObject json;
                String strAccessToken, strTokenType, strRefreshToken, strExpiresIn, strJti;

                // Convert String to json object
                json = new JSONObject(responseText);
                strAccessToken = json.getString(ACCESS_TOKEN_STRING);
                strTokenType = json.getString(TOKEN_TYPE_STRING);
                strRefreshToken = json.getString(REFRESH_TOKEN_STRING);
                strExpiresIn = json.getString(EXPIRES_IN_STRING);
                strJti = json.getString(JTI_STRING);
                Log.v(TAG, "JSON read");

                storeNewToken(context,
                        strAccessToken,
                        strTokenType,
                        strRefreshToken,
                        strExpiresIn,
                        strJti);


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

    private boolean doRetrieveToken(String username, String password) throws Exception {

        final String[] loginParamsValue = {
                GRANT_TYPE_PW,
                username,
                password,
                APPLICATION,
                TENANT
        };

        URL url = new URL(LOGIN_POST_URL);
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

            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // Open communications link (network traffic occurs here).
            connection.connect();
            DataOutputStream os = new DataOutputStream(connection.getOutputStream());
            os.writeBytes(getParams(loginParamsName, loginParamsValue));
            int responseCode = connection.getResponseCode();
            Log.v(TAG, "Sending Retrieve Token Request..");

            // BAD CREDENTIALS
            if(responseCode == HttpsURLConnection.HTTP_INTERNAL_ERROR){
                Log.e(TAG, "Wrong credentials");
                sendMessage(broadcastEventName, HttpLogin.MessageType.MSG_BAD_CREDENTIALS);
                return false;
            }

            // OTHER ERROR TYPE
            if(responseCode != HttpsURLConnection.HTTP_OK){
                Log.e(TAG, "Other error");
                sendMessage(broadcastEventName, HttpLogin.MessageType.MSG_ERR);
                return false;
            }

            // Get the response body as an InputStream.
            stream = connection.getInputStream();
            if (stream != null) {
                String responseText = getResponseText(stream);

                JSONObject json;
                // Convert String to json object
                json = new JSONObject(responseText);
                String strAccessToken = json.getString(ACCESS_TOKEN_STRING);
                String strTokenType = json.getString(TOKEN_TYPE_STRING);
                String strRefreshToken = json.getString(REFRESH_TOKEN_STRING);
                String strExpiresIn = json.getString(EXPIRES_IN_STRING);
                String strJti = json.getString(JTI_STRING);
                Log.v(TAG, "JSON read");

                storeNewToken(context,
                        strAccessToken,
                        strTokenType,
                        strRefreshToken,
                        strExpiresIn,
                        strJti);

            } else{
                throw new IOException("Response stream: null");
            }
            return true;
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
