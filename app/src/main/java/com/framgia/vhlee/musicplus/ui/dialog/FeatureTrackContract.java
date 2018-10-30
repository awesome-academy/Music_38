package com.framgia.vhlee.musicplus.ui.dialog;

import com.framgia.vhlee.musicplus.data.model.Track;

public interface FeatureTrackContract {

    interface View {
        void onFail();

        void onAddTracksSuccess();

        void existTrackInFavorites(String mesage);
    }

    interface Presenter {
        void addFavariteTrack(Track track);
    }
}
