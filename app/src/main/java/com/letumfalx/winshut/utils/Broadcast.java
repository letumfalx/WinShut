package com.letumfalx.winshut.utils;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.sql.*;

/**
 * Created by LetumFalx on 07/24/17 024.
 */

public final class Broadcast {

    private static DatagramSocket client = null;
    private static int port = 12206;


    private static Set<DataEventListener> dataEventListeners = new HashSet<>();
    private static Set<ConnectionEventListener> connectionEventListeners = new HashSet<>();
    private static Thread receiver = null;


    public static boolean isRunning() {
        return receiver != null ? receiver.isAlive() && !receiver.isInterrupted() : false;
    }

    public static void start() {
        try {
            stop();
            client = new DatagramSocket(port);
            receiver = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        for(;;) {
                            DatagramPacket dpack = new DatagramPacket(new byte[256], 256);
                            client.receive(dpack);
                            try {
                                Data dt = new Data(dpack.getData(), dpack.getLength());
                                for(DataEventListener del : dataEventListeners) {
                                    del.onReceive(new DataEvent(dt, dpack.getAddress()));
                                }
                            }
                            catch(IllegalArgumentException ex) {
                            }
                            Thread.sleep(0);
                        }
                    }
                    catch(InterruptedException | IOException ex) {
                    }
                }
            });
            receiver.start();
        }
        catch(IOException ex) {
            for(ConnectionEventListener cel : connectionEventListeners) {
                cel.onError(ex.getMessage());
            }
        }
    }

    public static void stop() {
        if(receiver != null) {
            if(receiver.isAlive() || !receiver.isInterrupted()) {
                receiver.interrupt();
            }
            receiver = null;
        }

        if(client != null) {
            if(!client.isClosed()) client.close();
            client = null;
        }

        for(ConnectionEventListener cel : connectionEventListeners) {
            cel.onStop(new ConnectionEvent());
        }
    }

    public static void clearConnectionEventListener() {
        connectionEventListeners.clear();
    }

    public static void clearDataEventListener() {
        dataEventListeners.clear();
    }

    public static void addConnectionEventListener(ConnectionEventListener listener) {
        connectionEventListeners.add(listener);
    }

    public static void addDataEventListener(DataEventListener listener) {
        dataEventListeners.add(listener);
    }

    public static void removeConnectionEventListener(ConnectionEventListener listener) {
        connectionEventListeners.remove(listener);
    }

    public static void removeDataEventListener(DataEventListener listener) {
        dataEventListeners.remove(listener);
    }

}
