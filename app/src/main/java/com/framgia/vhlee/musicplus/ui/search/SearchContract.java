package com.framgia.vhlee.musicplus.ui.search;

import com.framgia.vhlee.musicplus.data.model.Track;

import java.util.List;

public interface SearchContract {
    interface Presenter {
        void searchTracks(String api);
    }

    interface View {
        void showResult(List<Track> tracks);

        void showNoResult(String message);
    }
}
