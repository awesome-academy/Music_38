package com.framgia.vhlee.musicplus.ui.mymusic;

import android.content.Context;

import com.framgia.vhlee.musicplus.data.model.Track;

import java.util.List;

public interface MyMusicContract {
    interface Presenter {
        void loadOffline();
    }

    interface View {
        Context getContext();
        void onSuccess(List<Track> tracks);

        void onFailure(String message);
    }
}
