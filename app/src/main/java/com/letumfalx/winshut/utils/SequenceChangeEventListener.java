package com.letumfalx.winshut.utils;

/**
 * Created by LetumFalx on 07/27/17 027.
 */

public interface SequenceChangeEventListener {

    void onSequenceChange(SequenceChangeEvent event);
    void onError(String message);

}
