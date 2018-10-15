package com.framgia.vhlee.musicplus.ui.adapter;

import android.support.annotation.IntDef;

import com.framgia.vhlee.musicplus.data.model.Track;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface MediaPlayerListener {
    @IntDef({State.LOOP, State.PLAYING, State.PAUSED, State.NO_LOOP, State.COMPLETED})
    @Retention(RetentionPolicy.SOURCE)
    @interface State {
        int PLAYING = 0;
        int PAUSED = 1;
        int LOOP = 2;
        int NO_LOOP = 3;
        int COMPLETED = 4;
    }

    void onPositionChanged(int position);

    void onStateChanged(@State int state);

    void onPlaybackCompleted();

    void onTrackChange(Track track);
}
