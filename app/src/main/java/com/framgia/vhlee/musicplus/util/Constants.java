package com.framgia.vhlee.musicplus.util;


import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class Constants {
    public static class ApiConfig {
        public static final String CLIENT_ID = "&client_id=";
        public static final String BASE_URL_GENRES = "https://api-v2.soundcloud.com/charts?kind=top&genre=";
        public static final String BASE_URL_TRACK = "http://api.soundcloud.com/tracks";
        public static final String GENRES_ALL_MUSIC = "soundcloud:genres:all-music";
        public static final String GENRES_ALL_AUDIO = "soundcloud:genres:all-audio";
        public static final String GENRES_ALTERNATIVEROCK = "soundcloud:genres:alternativerock";
        public static final String GENRES_AMBIENT = "soundcloud:genres:ambient";
        public static final String GENRES_CLASSICAL = "soundcloud:genres:classical";
        public static final String GENRES_COUNTRY = "soundcloud:genres:country";
        public static final String PARAMETER_LIMIT = "&limit=";
        public static final String PARAMETER_OFFSET = "&offset=";
        public static final String PARAMETER_SEARCH = "&q=";
        public static final String NAME_STREAM = "stream";
        public static final String SPLASH = "/";
        public static final String QUESTION_MARK = "?";
    }

    public static class Genre {
        @Retention(RetentionPolicy.SOURCE)
        @StringDef({
                ALL_MUSIC, ALL_AUDIO, ALTERNATIVEROCK,
                AMBIENT, CLASSICAL, COUNTRY
        })
        public @interface GenresName {
        }

        public static final String ALL_MUSIC = "All Music";
        public static final String ALL_AUDIO = "All Audio";
        public static final String ALTERNATIVEROCK = "Alternativerock";
        public static final String AMBIENT = "Ambient";
        public static final String CLASSICAL = "Classical";
        public static final String COUNTRY = "Country";
    }

    public static class Track {
        public static final String COLLECTION = "collection";
        public static final String TRACK = "track";
        public static final String PUBLISHER = "publisher_metadata";
        public static final String ID = "id";
        public static final String TITLE = "title";
        public static final String ARTIST = "artist";
        public static final String ARTWORK_URL = "artwork_url";
        public static final String ARTWORK_DEFAULT_SIZE = "large";
        public static final String ARTWORK_MEDIUM_SIZE = "t300x300";
        public static final String ARTWORK_MAX_SIZE = "t500x500";
    }

    public static class Common {
        public static final int INDEX_UNIT = 1;
        public static final String EXTRA_GENRES = "genres";
    }
}
