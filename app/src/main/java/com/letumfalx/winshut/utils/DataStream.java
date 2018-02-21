package com.letumfalx.winshut.utils;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Created by LetumFalx on 07/25/17 025.
 */

public class DataStream {

    public final Socket getClient() {
        return client;
    }

    public final BufferedReader getIn() {
        return in;
    }

    public final PrintWriter getOut() {
        return out;
    }

    public final boolean isConnected() {
        return client != null ? !client.isClosed() ? client.isConnected() : false : false;
    }

    private final Socket client;
    private BufferedReader in;
    private PrintWriter out;

    public DataStream() throws IOException{
        this.client = new Socket();
    }

    public boolean connect(InetSocketAddress address) {
        try {
            client.connect(address, 1500);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream());
            send(new Data("connect", "connect"));
            for (long start = System.currentTimeMillis();
                 System.currentTimeMillis() - start < 2000; ) {
                if (read() != null) {
                    return true;
                }
            }
        }
        catch(IOException ex) {}
        return false;
    }

    public void close() {
        try {
            if (!client.isClosed()) {
                client.close();
            }
        }
        catch(IOException ex) {

        }
    }

    public boolean isClosed() {
        return client != null ? client.isClosed() : true;
    }

    public Data read() throws IOException{
        try {
            String rcv = "";
            for(int b = in.read(); b > -1 && b != 10; b = in.read()) {
                char c = (char)b;
                rcv += c;
            }
            return new Data(rcv);
        }
        catch(IllegalArgumentException ex) {
            return null;
        }
    }

    public boolean send(Data data) {
        out.print(data.toString() + (char)10);
        return !out.checkError();
    }

    public boolean send(String key, String value) {
        return send(new Data(key, value));
    }

    public boolean send(String key, String... values) {
        if(values.length <= 0) return false;
        return send(new Data(key, values));
    }

}
