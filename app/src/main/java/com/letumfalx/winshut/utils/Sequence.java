package com.letumfalx.winshut.utils;

import android.util.Log;

import java.util.*;

/**
 * Created by LetumFalx on 07/26/17 026.
 */

public final class Sequence {

    private static SequenceList currentSequence = SequenceList.NoShutdown;
    private static SequenceType currentType = SequenceType.None;

    private static Set<SequenceChangeEventListener> sequenceChangeEventListeners = new HashSet<>();

    public final static SequenceList getCurrentSequence() {
        return currentSequence;
    }

    public final static SequenceType getCurrentType() {
        return currentType;
    }

    public final static SequenceList getSequence(int sequence) {
        for(SequenceList seq : SequenceList.values()) {
            if(seq.value == sequence) {
                return seq;
            }
        }
        return SequenceList.NoShutdown;
    }

    public final static SequenceType getType(int type) {
        for(SequenceType seq : SequenceType.values()) {
            if(seq.value == type) {
                return seq;
            }
        }
        return SequenceType.None;
    }

    public final static void clearSequenceChangeEventListener() {
        sequenceChangeEventListeners.clear();
    }

    public final static void addSequenceChangeEventListener(SequenceChangeEventListener listener) {
        sequenceChangeEventListeners.add(listener);
    }

    public final static void removeSequenceChangeEventListener(SequenceChangeEventListener listener) {
        sequenceChangeEventListeners.remove(listener);
    }

    public final static void setSequence(Data rcv) {
        if(!rcv.getKey().trim().equalsIgnoreCase("sequence")) {
            return;
        }
        if(rcv.get().trim().equals("-1")) {
            onError(rcv.get(1));
            return;
        }

        try {
            SequenceList _seq = Sequence.getSequence(Integer.parseInt(rcv.get().trim()));
            SequenceType _type = Sequence.getType(Integer.parseInt(rcv.get(1)));
            switch(_type) {
                case Fast:
                    if(_seq.equals(currentSequence) && _type.equals(currentType)) {
                        break;
                    }
                    currentSequence = _seq;
                    currentType = _type;
                    onSequenceChange(new SequenceChangeEvent(_seq));
                    break;
                case Scheduled:
                case Timed:
                    int hr = new Integer(rcv.get(2));
                    int min = new Integer(rcv.get(3));
                    int sec = new Integer(rcv.get(4));
                    currentSequence = _seq;
                    currentType = _type;
                    if(_type == SequenceType.Timed) {
                        onSequenceChange(new SequenceChangeEvent(_seq, hr, min, sec));
                    }
                    else {
                        onSequenceChange(new SequenceChangeEvent(_seq, new MyDate()));
                    }
                    break;
                case None:
                    currentSequence = _seq;
                    currentType = _type;
                    onSequenceChange(new SequenceChangeEvent());
                    break;
            }
        }
        catch(NumberFormatException | ArrayIndexOutOfBoundsException ex) {
            onError("Data read error.");
            return;
        }
    }

    private static void onSequenceChange(SequenceChangeEvent event) {
        for(SequenceChangeEventListener scl : sequenceChangeEventListeners) {
            scl.onSequenceChange(event);
        }
    }

    private static void onError(String message) {
        for(SequenceChangeEventListener scl : sequenceChangeEventListeners) {
            scl.onError(message);
        }
    }

    private Sequence(){}
}

