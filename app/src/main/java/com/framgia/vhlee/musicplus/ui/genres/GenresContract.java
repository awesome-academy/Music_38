package com.framgia.vhlee.musicplus.ui.genres;

import com.framgia.vhlee.musicplus.data.model.Track;

import java.util.List;

public interface GenresContract {

    interface View {
        void onLoadTracksSuccess(List<Track> tracks);
        void onLoadTracksFail(String message);
    }

    interface Presenter {
        void getTracks(String api);
    }
}
