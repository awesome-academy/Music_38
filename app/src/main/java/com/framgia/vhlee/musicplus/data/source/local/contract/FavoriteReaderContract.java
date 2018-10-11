package com.framgia.vhlee.musicplus.data.source.local.contract;

import android.provider.BaseColumns;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class FavoriteReaderContract {

    private FavoriteReaderContract() {

    }

    public static class FavoriteEntry implements BaseColumns {
        public static final String TABLE_NAME = "favorite";
        public static final String COLUMN_NAME_TRACK_ID = "id_track";
        public static final String COLUMN_NAME_IS_OFFLINE = "is_offline";
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TrackType.OFFLINE, TrackType.ONLINE})
    public @interface TrackType {
        int OFFLINE = 0;
        int ONLINE = 1;
    }
}
