package com.framgia.vhlee.musicplus.data.model;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({
        GenreKey.ALL_MUSIC, GenreKey.ALL_AUDIO, GenreKey.ALTERNATIVE,
        GenreKey.AMBIENT, GenreKey.CLASSICAL, GenreKey.COUNTRY
})
public @interface GenreKey {
    String ALL_MUSIC = "soundcloud:genres:all-music";
    String ALL_AUDIO = "soundcloud:genres:all-audio";
    String ALTERNATIVE = "soundcloud:genres:alternativerock";
    String AMBIENT = "soundcloud:genres:ambient";
    String CLASSICAL = "soundcloud:genres:classical";
    String COUNTRY = "soundcloud:genres:country";
}
