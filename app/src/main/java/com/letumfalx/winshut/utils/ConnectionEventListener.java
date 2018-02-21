package com.letumfalx.winshut.utils;

/**
 * Created by LetumFalx on 07/25/17 025.
 */

public interface ConnectionEventListener {

    void onConnecting(ConnectionEvent event);
    void onConnected(ConnectionEvent event);
    void onDisconnect(ConnectionEvent event);
    void onStop(ConnectionEvent event);
    void onError(String message);
}
