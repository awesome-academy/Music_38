package com.framgia.vhlee.musicplus.data.source.local.contract;

import android.provider.BaseColumns;

public final class FavoriteReaderContract {

    private FavoriteReaderContract() {

    }

    public static class FavoriteEntry implements BaseColumns {
        public static final String TABLE_NAME = "favorite";
        public static final String COLUMN_NAME_TRACK_ID = "id_track";
        public static final String COLUMN_NAME_IS_OFFLINE = "is_offline";
        public static final String COLUMN_NAME_ARTWORK_URL = "artwork_url";
        public static final String COLUMN_NAME_USER_NAME = "artist";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DOWNLOADABLE = "downloadable";
        public static final String COLUMN_NAME_DOWNLOAD_URL = "download_url";
    }
}
