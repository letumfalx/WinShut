package com.letumfalx.winshut.utils;

import java.net.InetAddress;

/**
 * Created by LetumFalx on 07/25/17 025.
 */

public class DataEvent {

    private Data data = null;
    private ConnectionEvent details = null;

    public final ConnectionEvent getConnectionDetails() {
        return details;
    }

    public final Data getData() {
        return data;
    }


    public DataEvent(Data data) {
        this.data = data;
    }

    public DataEvent(Data data, InetAddress remote) {
        this(data);
        details = new ConnectionEvent(remote);
    }

    public DataEvent(Data data, InetAddress remote, InetAddress local) {
        this(data);
        details = new ConnectionEvent(remote, local);
    }

    private DataEvent() {}

}
