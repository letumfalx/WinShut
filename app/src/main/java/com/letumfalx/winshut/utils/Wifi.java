package com.letumfalx.winshut.utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.util.*;

/**
 * Created by LetumFalx on 07/24/17 024.
 */

public final class Wifi {

    /**
     * The current active activity.
     */
    private static Activity currentActivity = null;

    /**
     * Sets the current activity.
     * @param activity the activity to be set
     */
    public final static void setCurrentActivity(Activity activity) {
        currentActivity = activity;
    }


    /**
     * Checks whether the WiFi is currently connected.
     * @return true if connected, false if not
     */
    public static final boolean isConnected() {
        if(currentActivity == null) {
            return false;
        }
        WifiManager wifiMgr =
                (WifiManager)currentActivity
                        .getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(wifiMgr == null) {
            return false;
        }

        if (wifiMgr.isWifiEnabled()) {

            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
            if(wifiInfo == null) {
                return false;
            }

            if( wifiInfo.getNetworkId() < 0){
                return false;
            }
            return true;
        }
        else {
            return false; // Wi-Fi adapter is OFF
        }



    }

    public final static void addConnectivityChangeListener(ConnectivityChangeListener action) {
        ConnectivityChangeReceiver.addListener(action);
    }

    public final static void removeConnectivityChangeListener(ConnectivityChangeListener action) {
        ConnectivityChangeReceiver.removeListener(action);
    }

    public final static void clearConnectivityChangeListener() {
        ConnectivityChangeReceiver.clearListeners();
    }

    public static class ConnectivityChangeReceiver extends BroadcastReceiver {

        private static final Set<ConnectivityChangeListener> listeners = new HashSet<>();
        private static final void clearListeners() {
            listeners.clear();
        }

        public final static void addListener(
                ConnectivityChangeListener connectivityChangeListener) {
            listeners.add(connectivityChangeListener);
        }

        public final static void removeListener(
                ConnectivityChangeListener connectivityChangeListener) {
            listeners.remove(connectivityChangeListener);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            /*
            ConnectivityManager conMan =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = conMan.getActiveNetworkInfo();
            if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI)
            */
            if(isConnected())
                for(ConnectivityChangeListener ccl : listeners) {
                    ccl.onConnect();
                }
            else
                for(ConnectivityChangeListener ccl : listeners) {
                    ccl.onDisconnect();
                }
        }
    }

    /**
     * Makes the constructor private so it will not be instantialized.
     */
    private Wifi() {}

}
