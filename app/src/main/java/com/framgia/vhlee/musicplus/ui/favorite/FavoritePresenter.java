package com.framgia.vhlee.musicplus.ui.favorite;

import com.framgia.vhlee.musicplus.data.model.Track;
import com.framgia.vhlee.musicplus.data.repository.TrackRepository;
import com.framgia.vhlee.musicplus.data.source.TrackDataSource;

import java.util.List;

public class FavoritePresenter implements FavoriteContract.Presenter {
    private FavoriteContract.View mView;
    private TrackRepository mRepository;

    public FavoritePresenter(TrackRepository repository, FavoriteContract.View view) {
        mView = view;
        mRepository = repository;
    }

    @Override
    public void getFavotiteTracks() {
        mRepository.getFavotiteTracks(new TrackDataSource.DataCallback<Track>() {
            @Override
            public void onSuccess(List<Track> datas) {
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
        mRepository.deleteFavoriteTrack(track, new TrackDataSource.DataCallback<Boolean>() {
            @Override
            public void onSuccess(List<Boolean> datas) {
                mView.onDeleteTracksSuccess(datas.get(0));
            }

            @Override
            public void onFail(String mesage) {
                mView.onFail(mesage);
            }
        });
    }
}
