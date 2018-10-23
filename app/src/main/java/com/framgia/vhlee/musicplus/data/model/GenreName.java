package com.framgia.vhlee.musicplus.data.model;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({
        GenreName.ALL_MUSIC, GenreName.ALL_AUDIO, GenreName.ALTERNATIVE,
        GenreName.AMBIENT, GenreName.CLASSICAL, GenreName.COUNTRY
})
public @interface GenreName {
    String ALL_MUSIC = "All music";
    String ALL_AUDIO = "All audio";
    String ALTERNATIVE = "Alternative rock";
    String AMBIENT = "Ambient";
    String CLASSICAL = "Classical";
    String COUNTRY = "Country";

}
