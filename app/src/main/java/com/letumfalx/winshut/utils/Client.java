package com.letumfalx.winshut.utils;

import java.io.*;
import java.net.*;
import java.util.*;


/**
 * Created by LetumFalx on 07/25/17 025.
 */

public final class Client {

    public static DataStream getStream() {
        return stream;
    }

    private static DataStream stream = null;
    private static Timer updater = null;
    private static Thread receiver = null;

    private static int port = 12207;

    public final static int getPort() {
        return port;
    };

    public final static void setPort(int port) {
        if(port < 0 && port > Short.MAX_VALUE - Short.MIN_VALUE) {
            return;
        }
        if(isRunning()) {
            return;
        }
        Client.port = port;
    }

    public final static boolean isRunning() {
        return stream != null ? !stream.isClosed() && receiver.isAlive()
                && !receiver.isInterrupted() : false;
    }
    public final static boolean isConnected() {
        return stream != null ? stream.isConnected() : false;
    }

    public final static void start(String ipAddress, int portNumber) {
        setPort(portNumber);
        start(ipAddress);
    }

    public final static void start(final String ipAddress) {
        silentStop();
        receiver = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    stream = new DataStream();

                    InetSocketAddress address = new InetSocketAddress(ipAddress, port);


                    final ConnectionEvent tmp_cel
                            = new ConnectionEvent(address.getAddress(), stream.getClient().getLocalAddress());
                    for(ConnectionEventListener cel : connectionEventListeners) {
                        cel.onConnecting(tmp_cel);
                    }

                    if(!stream.connect(address)) {
                        for (ConnectionEventListener cel : connectionEventListeners) {
                            cel.onError("Failed to connect to " + ipAddress + ".");
                        }
                        stream.close();
                        return;
                    }

                    updater = new Timer();
                    updater.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if(stream != null ? !stream.isClosed() : false) {
                                stream.send("update", Long.toString(System.currentTimeMillis()));
                            }
                        }
                    }, 0, 1000);

                    for(ConnectionEventListener cel : connectionEventListeners) {
                        cel.onConnected(tmp_cel);
                    }

                    for(;;) {
                        Data read = stream.read();
                        if(read != null) {

                            DataEvent tmp_de = new DataEvent(read,
                                    stream.getClient().getInetAddress(),
                                    stream.getClient().getLocalAddress());
                            for(DataEventListener del : dataEventListeners) {
                                del.onReceive(tmp_de);
                            }
                        }
                    }
                }
                catch(IOException ex) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            silentStop();
                            for(ConnectionEventListener cel : connectionEventListeners) {
                                cel.onDisconnect(null);
                            }
                        }
                    }).start();
                }
            }
        });
        receiver.start();
    }

    public final static void stop() {
        silentStop();
        for(ConnectionEventListener cel : connectionEventListeners) {
            cel.onStop(new ConnectionEvent());
        }
    }

    private static void silentStop() {
        if(updater != null) {
            updater.cancel();
            updater = null;
        }

        if(receiver != null) {
            if(!receiver.isInterrupted() || receiver.isAlive()) receiver.interrupt();
            receiver = null;
        }

        if(stream != null) {
            stream.close();
            stream = null;
        }
    }



    private Client() {}

    private final static Set<ConnectionEventListener> connectionEventListeners = new HashSet<>();

    public final static void clearConnectionEventListener() {
        connectionEventListeners.clear();
    }

    public final static void addConnectionEventListener(ConnectionEventListener listener) {
        connectionEventListeners.add(listener);
    }

    public final static void removeConnectionEventListener(ConnectionEventListener listener) {
        connectionEventListeners.remove(listener);
    }

    private final static Set<DataEventListener> dataEventListeners = new HashSet<>();

    public final static void clearDataEventListener() {
        dataEventListeners.clear();
    }

    public final static void addDataEventListener(DataEventListener listener) {
        dataEventListeners.add(listener);
    }

    public final static void removeDataEventListener(DataEventListener listener) {
        dataEventListeners.remove(listener);
    }

}
