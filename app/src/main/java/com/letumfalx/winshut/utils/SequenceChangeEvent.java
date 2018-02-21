package com.letumfalx.winshut.utils;

import java.util.Date;

/**
 * Created by LetumFalx on 07/27/17 027.
 */

public class SequenceChangeEvent {

    public final SequenceList sequence;
    public final SequenceType type;
    public final Date executionDate;
    public final int hour;
    public final int minute;
    public final int second;
    public final long totalMillis;

    private SequenceChangeEvent(SequenceList seq, SequenceType type,
                                Date execDate, int hr, int min, int sec) {
        sequence = seq;
        this.type = type;
        executionDate = execDate;
        hour = hr;
        minute = min;
        second = sec;
        long millis = (hr * 3600 + min * 60 + sec) * 1000;
        totalMillis = millis >= 0 ? millis : -1;

    }

    private SequenceChangeEvent(SequenceList seq, SequenceType type) {
        this(seq, type, null, -1, -1, -1);
    }

    public SequenceChangeEvent() {
        this(SequenceList.NoShutdown, SequenceType.None);
    }

    public SequenceChangeEvent(SequenceList seq) {
        this(seq, SequenceType.Fast);
    }

    public SequenceChangeEvent(SequenceList seq, long timeout) {
        this(seq, (int)(timeout / 3600000), (int)((timeout % 3600000) / 60000),
                (int)(((timeout % 3600000) % 60000) / 1000));
    }

    public SequenceChangeEvent(SequenceList seq, int hr, int min, int sec) {
        this(seq, SequenceType.Timed, null, hr, min, sec);
    }

    public SequenceChangeEvent(SequenceList seq, Date date) {
        this(seq, SequenceType.Scheduled, date, -1, -1, -1);
    }

    public SequenceChangeEvent(SequenceList seq, MyDate date) {
        this(seq, date.getDate());
    }

}
