package com.polimi.movecare_r01.logic.http;

import android.content.Context;
import android.util.Log;

import com.polimi.movecare_r01.dao.preferences.LoginPreferences;

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

public class HttpLoginManager {
    private static final String TAG = "HttpLoginManager";

    private Context context;

    private final static String USERNAME_STRING = "username";
    private final static String PASSWORD_STRING = "password";
    private final static String APPLICATION_STRING = "application";
    private final static String GRANT_TYPE_STRING = "grant_type";
    private final static String TENANT_STRING = "tenant";

    private final static String[] paramsName = {
            GRANT_TYPE_STRING,
            USERNAME_STRING,
            PASSWORD_STRING,
            APPLICATION_STRING,
            TENANT_STRING
    };

    private final static String LOGIN_POST_URL = "http://api.movecare-project.eu/oauth/token";
    private final static String GET_USERID_URL = "http://api.movecare-project.eu/movecare/datacenter/v1/userid";
    private final static String GET_ALL_USER_DATA_URL = "http://api.movecare-project.eu/usercontrol/data/v1/api/users/?user-uuid=";

    public HttpLoginManager(Context context){
        this.context = context;
    }

    public void login(String[] paramsList) throws IOException {

        URL url = new URL(LOGIN_POST_URL);

        InputStream stream = null;
        // Cambiare in Https
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(3000);
            connection.setConnectTimeout(3000);
            connection.setRequestMethod("POST");

            connection.setDoInput(true);
            connection.setDoOutput(true);

            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // Open communications link (network traffic occurs here).
            connection.connect();

            DataOutputStream os = new DataOutputStream(connection.getOutputStream());
            os.writeBytes(getParams(paramsName, paramsList));

            int responseCode = connection.getResponseCode();
            Log.e(TAG, "Sending Login Request..");
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }

            // Retrieve the response body as an InputStream.
            stream = connection.getInputStream();

            if (stream != null /*&& checkServerResponse(stream)*/) {
                // store token in SharedPreferences
                Log.e(TAG,"Data received. Validating ...");

                StringBuilder result = new StringBuilder();

                Reader reader = new InputStreamReader(stream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(reader);

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line);
                }

                String responseText = result.toString();

                JSONObject json;
                String strAccessToken, strTokenType, strRefreshToken, strExpiresIn, strJti;
                try {
                    // Convert String to json object
                    json = new JSONObject(responseText);
                    strAccessToken = json.getString("access_token");
                    strTokenType = json.getString("token_type");
                    strRefreshToken = json.getString("refresh_token");
                    strExpiresIn = json.getString("expires_in");
                    strJti = json.getString("jti");
                    Log.e(TAG, "JSON read");

                    // Store token data within shared preferences
                    LoginPreferences logPref = new LoginPreferences();
                    logPref.storeVariable(context, LoginPreferences.Variable.TEST_TOKEN, strAccessToken);

                    /*LoginPreferences logPref = new LoginPreferences();
                    logPref.storeVariable(context, LoginPreferences.Variable.ACCESS_TOKEN, strAccessToken);
                    logPref.storeVariable(context, LoginPreferences.Variable.TOKEN_TYPE, strTokenType);
                    logPref.storeVariable(context, LoginPreferences.Variable.REFRESH_TOKEN, strRefreshToken);
                    logPref.storeVariable(context, LoginPreferences.Variable.EXPIRES_IN, strExpiresIn);
                    logPref.storeVariable(context, LoginPreferences.Variable.JTI, strJti);
                    Log.e(TAG, "Token stored in Shared Preferences");*/

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

    public void retrieveUserID() throws IOException {
        // Possible only if token is stored --> get token
        LoginPreferences logPrefs = new LoginPreferences();
        //String accessToken = logPrefs.getVariable(context, LoginPreferences.Variable.ACCESS_TOKEN);
        String accessToken = logPrefs.getVariable(context, LoginPreferences.Variable.TEST_TOKEN);
        if(accessToken == null){
            Log.e(TAG, "Token not available");
            return;
        }

        URL url = new URL(GET_USERID_URL);

        InputStream stream = null;
        // Cambiare in Https
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
            Log.e(TAG, "Sending GET_USER_ID Request..");
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }

            // Retrieve the response body as an InputStream.
            stream = connection.getInputStream();

            if (stream != null) {
                // store token in SharedPreferences
                Log.e(TAG,"Data received. Validating ...");

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
                    Log.e(TAG, "JSON read");

                    // Store token data within shared preferences
                    LoginPreferences logPref = new LoginPreferences();
                    logPref.storeVariable(context, LoginPreferences.Variable.UUID, strUserID);
                    Log.e(TAG, "UUID stored in Shared Preferences");

                    //retrieveAllUserData();

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

    public void retrieveAllUserData() throws IOException {
        // Possible only if token is stored --> get token
        LoginPreferences logPrefs = new LoginPreferences();
        String userId = logPrefs.getVariable(context, LoginPreferences.Variable.UUID);
        //String accessToken = logPrefs.getVariable(context, LoginPreferences.Variable.ACCESS_TOKEN);
        String accessToken = logPrefs.getVariable(context, LoginPreferences.Variable.TEST_TOKEN);
        if(userId == null || accessToken == null){
            Log.e(TAG, "UserID or AccessToken not available");
            return;
        }

        String tmpURL = GET_ALL_USER_DATA_URL+userId;
        URL url = new URL(GET_ALL_USER_DATA_URL+userId);

        InputStream stream = null;
        // Cambiare in Https
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(3000);
            connection.setConnectTimeout(3000);
            connection.setRequestMethod("GET");

            connection.setDoInput(true);
            connection.setDoOutput(true);

            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer "+accessToken);

            // Open communications link (network traffic occurs here).
            connection.connect();

            int responseCode = connection.getResponseCode();
            Log.e(TAG, "Sending GET_ALL_USER_DATA Request..");
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }

            // Retrieve the response body as an InputStream.
            stream = connection.getInputStream();

            if (stream != null) {
                // store token in SharedPreferences
                Log.e(TAG,"Data received. Validating ...");

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
                    Log.e(TAG, "JSON read");

                    // Store token data within shared preferences
                    LoginPreferences logPref = new LoginPreferences();
                    logPref.storeVariable(context, LoginPreferences.Variable.USERNAME, strUsername);
                    logPref.storeVariable(context, LoginPreferences.Variable.EMAIL, strEmail);
                    Log.e(TAG, "User data stored in Shared Preferences");

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
