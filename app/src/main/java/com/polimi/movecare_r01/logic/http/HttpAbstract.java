package com.polimi.movecare_r01.logic.http;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static android.content.Context.CONNECTIVITY_SERVICE;

public abstract class HttpAbstract {

    protected enum ConnectionType{
        WIFI,
        MOBILE_DATA,
        OTHER
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
}
