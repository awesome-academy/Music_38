package com.framgia.vhlee.musicplus.ui.home;

import com.framgia.vhlee.musicplus.data.model.Track;
import com.framgia.vhlee.musicplus.data.repository.TrackDataRepository;
import com.framgia.vhlee.musicplus.data.source.TrackDataSource;

import java.util.List;

public class HomePresenter implements HomeContract.Presenter {
    private HomeContract.View mView;
    private TrackDataRepository mRepository;

    public HomePresenter(HomeContract.View view, TrackDataRepository repository) {
        mView = view;
        mRepository = repository;
    }

    @Override
    public void loadHighlight(String url) {
        mRepository.getTracks(url, new TrackDataSource.DataCallback<Track>() {
            @Override
            public void onSuccess(List<Track> tracks) {
                mView.showHighLight(tracks);
            }

            @Override
            public void onFail(String mesage) {
                mView.showNoHighlight(mesage);
            }
        });
    }

    @Override
    public void loadRecent(String url) {
        mRepository.getDetailTrack(url, new TrackDataSource.DataCallback<Track>() {
            @Override
            public void onSuccess(List<Track> datas) {
                mView.showRecent(datas);
            }

            @Override
            public void onFail(String mesage) {
                mView.showRecentFail(mesage);
            }
        });
    }
}
