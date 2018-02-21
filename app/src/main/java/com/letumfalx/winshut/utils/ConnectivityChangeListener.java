package com.letumfalx.winshut.utils;

/**
 * Created by LetumFalx on 07/24/17 024.
 */

public interface ConnectivityChangeListener {

    /**
     * What to do on WiFi connect.
     */
    void onConnect();

    /**
     * What to do on WiFi disconnect.
     */
    void onDisconnect();

}
