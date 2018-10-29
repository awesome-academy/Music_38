package com.framgia.vhlee.musicplus.util;

import com.framgia.vhlee.musicplus.BuildConfig;
import com.framgia.vhlee.musicplus.data.model.GenreKey;

public class StringUtil {
    public static String append(String... strings) {
        StringBuilder builder = new StringBuilder();
        for (String string : strings) {
            builder.append(string);
        }
        return builder.toString();
    }

    public static String initGenreApi(@GenreKey String genreKey, int offset) {
        return StringUtil.append(Constants.BASE_URL_GENRES, genreKey,
                Constants.CLIENT_ID, BuildConfig.CLIENT_ID,
                Constants.PARAMETER_LIMIT, String.valueOf(Constants.LIMIT),
                Constants.PARAMETER_OFFSET, String.valueOf(offset));
    }

    public static String initDetailApi(long trackId) {
        return StringUtil.append(Constants.BASE_URL_TRACK, Constants.SPLASH,
                String.valueOf(trackId), Constants.QUESTION_MARK,
                Constants.CLIENT_ID, BuildConfig.CLIENT_ID);
    }

    public static String initStreamApi(long trackId) {
        return StringUtil.append(Constants.BASE_URL_TRACK, Constants.SPLASH,
                String.valueOf(trackId), Constants.SPLASH,
                Constants.NAME_STREAM, Constants.QUESTION_MARK,
                Constants.CLIENT_ID, BuildConfig.CLIENT_ID);
    }

    public static String initDownloadApi(String url) {
        return StringUtil.append(url, Constants.PARAMETER_ID, BuildConfig.CLIENT_ID);
    }

    public static String initSearchApi(String keyword, int offset) {
        return StringUtil.append(Constants.BASE_URL_TRACK,
                Constants.PARAMETER_ID, BuildConfig.CLIENT_ID,
                Constants.PARAMETER_SEARCH, keyword,
                Constants.PARAMETER_LIMIT, String.valueOf(Constants.LIMIT),
                Constants.PARAMETER_OFFSET, String.valueOf(offset));
    }
}
