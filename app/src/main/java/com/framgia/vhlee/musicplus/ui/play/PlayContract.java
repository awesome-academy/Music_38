package com.framgia.vhlee.musicplus.ui.play;

import com.framgia.vhlee.musicplus.data.model.Track;

import java.util.List;

public class PlayContract {

    interface View {
        void onFail(String message);

        void onAddTracksSuccess(List<String> id);
    }

    interface Presenter {
        void addFavariteTrack(Track track);
    }
}
