package com.framgia.vhlee.musicplus.ui.dialog;

import com.framgia.vhlee.musicplus.data.model.Track;

import java.util.List;

public interface FeatureTrackContract {

    interface View {
        void onFail(String message);

        void onAddTracksSuccess(List<String> id);
    }

    interface Presenter {
        void addFavariteTrack(Track track);
    }
}
