package com.letumfalx.winshut.utils;

import java.util.Comparator;

/**
 * Created by LetumFalx on 07/26/17 026.
 */

public enum SequenceType implements Comparable<SequenceType> {

    None (0, ""),
    Fast (1, "Fast"),
    Timed (2, "Timed"),
    Scheduled (3, "Scheduled");

    public final int value;
    public final String text;
    public final String data;

    SequenceType(int value, String text) {
        this.value = value;
        this.text = text;
        this.data = Integer.toString(value);
    }
}
