package com.letumfalx.winshut.utils;

import java.net.*;

/**
 * Created by LetumFalx on 07/25/17 025.
 */

public class ConnectionEvent {


    private InetAddress remote = null;

    public InetAddress getRemoteInetAddress() {
        return remote;
    }

    public String getRemoteIPAddress() {
        return remote != null ? remote.getHostAddress() : "";
    }

    public String getRemoteHostname() {
        return remote != null ? remote.getHostName() : "";
    }

    private InetAddress local = null;

    public InetAddress getLocalInetAddress() {
        return local;
    }

    public String getLocalIPAddress() {
        return local != null ? local.getHostAddress() : "";
    }

    public String getLocalHostname() {
        return local != null ? local.getHostName() : "";
    }

    public ConnectionEvent() {}

    public ConnectionEvent(InetAddress remote) {
        this.remote = remote;
    }

    public ConnectionEvent(InetAddress remote, InetAddress local) {
        this(remote);
        this.local = local;
    }

}
