package com.letumfalx.winshut.utils;

import android.app.Activity;

import java.util.Comparator;

/**
 * Created by LetumFalx on 07/26/17 026.
 */

public enum SequenceList implements Comparator<SequenceList>{

    NoShutdown (0, "No Shutdown"),
    Shutdown (1, "Shutdown"),
    Restart (2, "Restart"),
    Sleep (3, "Sleep"),
    Hibernate (4, "Hibernate"),
    LogOff (5, "Log Off"),
    LockUser (6, "Lock User");

    public final int value;
    public final String text;
    public final String data;

    SequenceList(int value, String text) {
        this.value = value;
        this.text = text;
        this.data = Integer.toString(value);
    }

    @Override
    public int compare(SequenceList sequenceList, SequenceList t1) {
        return sequenceList.value - t1.value;
    }
}
