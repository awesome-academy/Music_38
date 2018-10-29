package com.framgia.vhlee.musicplus.util;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreferences {
    private static final String PREFERENCES_NAME = "SharedPreferences";
    private static final int INDEX_0 = 0;
    private static final int INDEX_1 = 1;
    private static final int INDEX_2 = 2;
    private static final int NUMBER_OF_SAVED_TRACKS = 3;
    private static final String KEY_TRACK_1 = "key_track_1";
    private static final String KEY_TRACK_2 = "key_track_2";
    private static final String KEY_TRACK_3 = "key_track_3";
    private SharedPreferences mPreferences;

    public MySharedPreferences(Context context) {
        mPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public void addData(long idTrack) {
        long idTrack1 = getData(KEY_TRACK_1);
        long idTrack2 = getData(KEY_TRACK_2);
        putData(KEY_TRACK_3, idTrack2);
        putData(KEY_TRACK_2, idTrack1);
        putData(KEY_TRACK_1, idTrack);
    }

    public long[] getData() {
        long[] result = new long[NUMBER_OF_SAVED_TRACKS];
        result[INDEX_0] = getData(KEY_TRACK_1);
        result[INDEX_1] = getData(KEY_TRACK_2);
        result[INDEX_2] = getData(KEY_TRACK_3);
        return result;
    }

    private long getData(String key) {
        return mPreferences.getLong(key, 0);
    }

    private void putData(String key, long value) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putLong(key, value);
        editor.commit();
    }
}
