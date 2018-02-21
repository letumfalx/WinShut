package com.letumfalx.winshut.utils;

import java.util.*;

/**
 * Created by LetumFalx on 07/25/17 025.
 */

public class Data {



    /**
     * The byte string that will used as indicator that the data is from
     * the right sender.
     */
    private static String header = "\u0000\u0006\u0001\u0008" +
            "\u0001\u0009\u0009\u0006";

    /**
     * Sets the header sequence.
     * @param seq the array of bytes that will be used as the header
     * @throw IllegalArgumentException if bytes are greater than 64 in size
     */
    public static void setHeaderSequence(byte... seq) {
        if(seq.length > 64) throw
                new IllegalArgumentException("sequence too many, " +
                        "must be less than 65");
        header = new String(seq);
    }

    public static void setHeaderSequence(int... seq) {
        if(seq.length > 64) throw
                new IllegalArgumentException("sequence too many, " +
                        "must be less than 65");
        header = "";
        for(int b : seq) {
            char c = (char)b;
            header += c;
        }
    }

    /**
     * Sets the header sequence.
     * @param seq the array of bytes that will be used as the header
     * @throw IllegalArgumentException if bytes are greater than 64 in size
     */
    public static void setHeaderSequence(java.util.List<Byte> seq) {
        if(seq.size() > 64) throw
                new IllegalArgumentException("sequence too many, " +
                        "must be less than 65");
        header = "";
        for(Byte b : seq) {
            char c = (char)b.byteValue();
            header += b;
        }
    }

    /**
     * Gets the data header sequence.
     * @return data header sequence as a string
     */
    public static String getHeaderSequence() {
        return header;
    }

    /**
     * Checks if the first data has the same content with the second data.
     * @param d1 the first data
     * @param d2 the second data
     * @return true if both have the same content, false if different
     */
    public static boolean compare(Data d1, Data d2) {
        return d1.toString().equals(d2.toString());
    }

    /**
     * The unique identifier of the data.
     */
    private final String key;

    /**
     * The list of content values of the data.
     */
    private final ArrayList<String> values;

    /**
     * Creates an instance with specified key and a value.
     * @param key the unique identifier of the data
     * @param value the initial content value of the data
     */
    public Data(String key, String value) {
        this.key = key;
        this.values = new ArrayList<>();
        this.values.add(value);
    }

    /**
     * Creates an instance with specified key and an array of values.
     * @param key the unique identifier of the data
     * @param value the array containing the initial content values of the data
     */
    public Data(String key, String... value) {
        this.key = key;
        this.values = new ArrayList<>();
        this.values.addAll(Arrays.asList(value));
    }

    /**
     * Creates an instance with specified key and a collection of values.
     * @param key the unique identifier of the data
     * @param values the collection of strings containing the initial content values of the data
     */
    public Data(String key, Collection<String> values) {
        this.key = key;
        this.values = new ArrayList<>();
        this.values.addAll(values);
    }

    /**
     * Creates an instance that the keys and values will be parsed from a specified string.
     * @param data the string to be parsed.
     * @throws IllegalArgumentException this will be thrown when the specified string does not have
     * the data header sequence and incomplete data
     */
    public Data(String data) {

        if(!data.startsWith(header)) {
            throw new IllegalArgumentException("stray data");
        }
        data = data.substring(data.indexOf(header) + header.length());
        if(data.indexOf("\0") < 1)
            throw new IllegalArgumentException("missing keyword");

        String tmp = data.substring(0, data.indexOf("\0"));
        data = data.substring(data.indexOf("\0") + 1);

        if(data.indexOf("\0") < 1)
            throw new IllegalArgumentException("missing value/s");

        this.key = tmp;
        this.values = new ArrayList<>();

        while(data.indexOf("\0") > 0) {
            this.values.add(data.substring(0, data.indexOf("\0")));
            data = data.substring(data.indexOf("\0") + 1);
        }
    }

    public Data(byte[] data) {
        this(data, 0, data.length);
    }

    public Data(byte[] data, int length) {
        this(data, 0, length);
    }

    public Data(byte[] data, int offset, int length) {
        this(new String(data, offset, length));
    }



    public Data set(String value) {
        this.values.clear();
        this.values.add(value);
        return this;
    }

    public Data set(String... value) {
        this.values.clear();
        this.values.addAll(Arrays.asList(value));
        return this;
    }

    public Data set(Collection<String> values) {
        this.values.addAll(values);
        return this;
    }

    public Data add(String value) {
        this.values.add(value);
        return this;
    }

    public Data add(String... value) {
        this.values.addAll(Arrays.asList(value));
        return this;
    }

    public Data add(Collection<String> values) {
        this.values.addAll(values);
        return this;
    }

    public Data add(int index, String value) {
        this.values.add(index, value);
        return this;
    }

    public Data add(int index, String... value) {
        this.values.addAll(index, Arrays.asList(value));
        return this;
    }

    public Data add(int index, Collection<String> values) {
        this.values.addAll(index, values);
        return this;
    }

    public String get() {
        if(this.values.size() < 1)
            throw new IndexOutOfBoundsException("no value has been set yet");
        return this.values.get(0);
    }

    public String get(int index) {
        return this.values.get(index);
    }

    public byte[] getBytes() {
        return getData().getBytes();
    }

    public ArrayList<String> getAll() {
        return this.values;
    }

    public String getKey() {
        return this.key;
    }

    public String getData() {
        String fin = header + key + "\0";
        for(String s : values) {
            fin += s + "\0";
        }
        return fin;
    }

    @Override
    public String toString() {
        return getData();
    }

    public String getContext() {
        String fin = key + "\r\n";
        for(String s : values) {
            fin += "\t" + s + "\r\n";
        }
        return fin;
    }

    public boolean compare(Data other) {
        return other.toString().equals(this.toString());
    }
}
