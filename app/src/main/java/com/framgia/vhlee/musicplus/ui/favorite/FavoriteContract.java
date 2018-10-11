package com.framgia.vhlee.musicplus.ui.favorite;

import com.framgia.vhlee.musicplus.data.model.Track;

import java.util.List;

public interface FavoriteContract {

    interface View {
        void onGetTracksSuccess(List<Long> trackIds);

        void onFail(String message);

        void onDeleteTracksSuccess(List<String> numberRows);

        void showFavorite(List<Track> tracks);

        void showFavoriteFail(String message);
    }

    interface Presenter {
        void getFavotiteTracks();

        void deleteFavoriteTrack(Track track);

        void loadFavorite(String url);
    }
}
