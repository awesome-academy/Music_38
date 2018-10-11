package com.framgia.vhlee.musicplus.mediaplayer;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class MediaPlayerSetting {
    protected int mLoopType;

    @LoopType
    public int getLoopType() {
        return mLoopType;
    }

    public void setLoopType(@LoopType int loopType) {
        mLoopType = loopType;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({LoopType.NONE, LoopType.ONE, LoopType.ALL})
    public @interface LoopType {
        int NONE = 0;
        int ONE = 1;
        int ALL = 2;
    }
}
