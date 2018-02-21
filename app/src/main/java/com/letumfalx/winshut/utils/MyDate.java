package com.letumfalx.winshut.utils;

/**
 * Created by LetumFalx on 07/28/17 028.
 */

import java.text.*;
import java.util.*;


public final class MyDate {

    private final Date date;

    public MyDate() {
        date = new Date();
    }

    public MyDate(Date date) {
        this.date = date;
    }

    public MyDate(long time) {
        this.date = new Date(time);
    }

    public MyDate(int month, int day, int year, int hr, int min, int sec)
            throws ParseException {

        date = new SimpleDateFormat("M.d.y.H.m.s").parse(
                Integer.toString(month) + "." +
                        Integer.toString(day) + "." +
                        Integer.toString(year) + "." +
                        Integer.toString(hr) + "." +
                        Integer.toString(min) + "." +
                        Integer.toString(sec) + "."
        );

    }

    public MyDate(String month, String day, String year,
                  String hr, String min, String sec) throws ParseException {

        date = new SimpleDateFormat("").parse(
                month + "." +
                        day + "." +
                        year + "." +
                        hr + "." +
                        min + "." +
                        sec + "."
        );
        if(date.getTime() < new Date().getTime())
            throw new IllegalArgumentException("date must be greater " +
                    "than current");

    }

    public MyDate(int hr, int min, int sec) throws ParseException {
        String today = new SimpleDateFormat("M.d.y.").format(new Date());
        date = new SimpleDateFormat("H.m.s").parse(today
                + Integer.toString(hr) + "."
                + Integer.toString(min) + "."
                + Integer.toString(sec) + ".");
    }

    public MyDate(String hr, String min, String sec) throws ParseException {
        String today = new SimpleDateFormat("M.d.y.").format(new Date());
        date = new SimpleDateFormat("H.m.s").parse(today
                + hr + "." + min + "." + sec);
    }

    public final Date getDate() {
        return date;
    }

    public final int getMonth() {
        return Integer.parseInt(new SimpleDateFormat("M").format(date));
    }

    public final int getDay() {
        return Integer.parseInt(new SimpleDateFormat("d").format(date));
    }

    public final int getYear() {
        return Integer.parseInt(new SimpleDateFormat("y").format(date));
    }

    public final int getHour() {
        return Integer.parseInt(new SimpleDateFormat("H").format(date));
    }

    public final int getMinute() {
        return Integer.parseInt(new SimpleDateFormat("m").format(date));
    }

    public final int getSecond() {
        return Integer.parseInt(new SimpleDateFormat("s").format(date));
    }

    @Override
    public final String toString() {
        return getString();
    }

    public final String getString() {
        return getString("M.d.y.H.m.s");
    }

    public final String getString(String format) {
        return new SimpleDateFormat(format).format(date);
    }

    public final long getTime() {
        return date.getTime();
    }

}

