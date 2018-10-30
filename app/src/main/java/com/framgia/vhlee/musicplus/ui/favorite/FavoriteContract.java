package com.framgia.vhlee.musicplus.ui.favorite;

import com.framgia.vhlee.musicplus.data.model.Track;

import java.util.List;

public interface FavoriteContract {

    interface View {
        void onGetTracksSuccess(List<Track> tracks);

        void onFail(String message);

        void onDeleteTracksSuccess(boolean b);
    }

    interface Presenter {
        void getFavotiteTracks();

        void deleteFavoriteTrack(Track track);
    }
}
