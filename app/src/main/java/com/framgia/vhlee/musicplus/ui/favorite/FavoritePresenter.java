package com.framgia.vhlee.musicplus.ui.favorite;

import android.content.Context;

import com.framgia.vhlee.musicplus.data.model.Track;
import com.framgia.vhlee.musicplus.data.repository.TrackRepository;
import com.framgia.vhlee.musicplus.data.source.TrackDataSource;
import com.framgia.vhlee.musicplus.data.source.local.TrackLocalDataSource;
import com.framgia.vhlee.musicplus.data.source.remote.TrackRemoteDataSource;

import java.util.List;

public class FavoritePresenter implements FavoriteContract.Presenter {
    private FavoriteContract.View mView;
    private TrackRepository mRepository;

    public FavoritePresenter(Context context, FavoriteContract.View view) {
        mView = view;
        mRepository = TrackRepository.getsInstance(TrackRemoteDataSource.getsInstance(),
                TrackLocalDataSource.getInstance(context));
    }

    @Override
    public void getFavotiteTracks() {
        mRepository.getFavotiteTracks(new TrackDataSource.DataCallback<Long>() {
            @Override
            public void onSuccess(List<Long> datas) {
                mView.onGetTracksSuccess(datas);
            }

            @Override
            public void onFail(String mesage) {
                mView.onFail(mesage);
            }
        });
    }

    @Override
    public void deleteFavoriteTrack(Track track) {
        mRepository.deleteFavoriteTrack(track, new TrackDataSource.DataCallback<String>() {
            @Override
            public void onSuccess(List<String> datas) {
                mView.onDeleteTracksSuccess(datas);
            }

            @Override
            public void onFail(String mesage) {
                mView.onFail(mesage);
            }
        });
    }

    @Override
    public void loadFavorite(String url) {
        mRepository.getDetailTrack(url, new TrackDataSource.DataCallback<Track>() {
            @Override
            public void onSuccess(List<Track> datas) {
                mView.showFavorite(datas);
            }

            @Override
            public void onFail(String mesage) {
                mView.showFavoriteFail(mesage);
            }
        });
    }
}
