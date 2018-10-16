package com.framgia.vhlee.musicplus.ui.home;

import com.framgia.vhlee.musicplus.data.model.Track;

import java.util.List;

public interface HomeContract {
    interface Presenter {
        void loadHighlight(String url);

        void loadRecent();
    }

    interface View {
        void showHighLight(List<Track> tracks);

        void showNoHighlight(String message);

        void showRecent(List<Track> tracks);
    }
}
