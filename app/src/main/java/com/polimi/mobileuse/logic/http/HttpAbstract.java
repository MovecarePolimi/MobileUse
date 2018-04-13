package com.polimi.mobileuse.logic.http;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.polimi.mobileuse.dao.preferences.LoginPreferences;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLEncoder;

import static android.content.Context.CONNECTIVITY_SERVICE;

public abstract class HttpAbstract {
    private static final String TAG = "HttpAbstract";

    protected Context context;
    protected String broadcastEventName;
    protected enum ConnectionType{
        WIFI,
        MOBILE_DATA,
        OTHER
    };

    protected final static int TIMEOUT_VALUE    = 1000;
    protected final static String POST_STRING   = "POST";
    protected final static String GET_STRING   = "GET";

    private final static String USERNAME_STRING = "username";
    private final static String PASSWORD_STRING = "password";
    private final static String APPLICATION_STRING = "application";
    private final static String GRANT_TYPE_STRING = "grant_type";
    private final static String TENANT_STRING = "tenant";

    protected final static String ACCESS_TOKEN_STRING = "access_token";
    protected final static String TOKEN_TYPE_STRING = "token_type";
    protected final static String REFRESH_TOKEN_STRING = "refresh_token";
    protected final static String EXPIRES_IN_STRING = "expires_in";
    protected final static String JTI_STRING = "jti";

    protected final static String[] refreshTokenParamsName = {
            GRANT_TYPE_STRING,
            REFRESH_TOKEN_STRING,
            APPLICATION_STRING,
            TENANT_STRING
    };

    protected final static String[] loginParamsName = {
            GRANT_TYPE_STRING,
            USERNAME_STRING,
            PASSWORD_STRING,
            APPLICATION_STRING,
            TENANT_STRING
    };

    protected boolean isConnected(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    // return 1 if Wi-Fi, 2 if mobile data, otherwise 0
    protected ConnectionType getConnectionType(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isWiFi = isConnected(context) && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
        if(isWiFi){
            return ConnectionType.WIFI;
        }
        boolean isMobileData = isConnected(context) && activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
        if(isMobileData){
            return ConnectionType.MOBILE_DATA;
        }
        return ConnectionType.OTHER;
    }

    protected void enableMobileData(Context context, boolean enabled) throws Exception{
        final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Class conmanClass = null;
        try {
            conmanClass = Class.forName(conman.getClass().getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }
        final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
        iConnectivityManagerField.setAccessible(true);
        final Object iConnectivityManager = iConnectivityManagerField.get(conman);
        final Class iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
        final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
        setMobileDataEnabledMethod.setAccessible(true);
        setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
    }

    protected void storeNewToken(Context context,
                                 String accessToken,
                                 String tokenType,
                                 String refreshToken,
                                 String expiresIn,
                                 String jti ){
        LoginPreferences logPref = new LoginPreferences();

        logPref.storeVariable(context, LoginPreferences.Variable.ACCESS_TOKEN, accessToken);
        logPref.storeVariable(context, LoginPreferences.Variable.TOKEN_TYPE, tokenType);
        logPref.storeVariable(context, LoginPreferences.Variable.REFRESH_TOKEN, refreshToken);
        logPref.storeVariable(context, LoginPreferences.Variable.EXPIRES_IN, expiresIn);
        logPref.storeVariable(context, LoginPreferences.Variable.JTI, jti);
    }

    protected String getParams(String[] paramsName, String[] paramsValue) throws UnsupportedEncodingException
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

    protected String getResponseText(InputStream stream) throws IOException {

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

    protected void sendMessage(String broadcasterName, HttpLogin.MessageType msg) {
        Log.v(TAG, "Send broadcasting message");
        //Intent intent = new Intent("custom-event-name");
        Intent intent = new Intent(broadcasterName);

        intent.putExtra("message", msg);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
