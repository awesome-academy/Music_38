package com.framgia.vhlee.musicplus.ui.mymusic;

import com.framgia.vhlee.musicplus.data.model.Track;
import com.framgia.vhlee.musicplus.data.repository.TrackRepository;
import com.framgia.vhlee.musicplus.data.source.TrackDataSource;

import java.util.List;

public class MyMusicPresenter implements MyMusicContract.Presenter {
    private MyMusicContract.View mView;
    private TrackRepository mRepository;

    public MyMusicPresenter(TrackRepository repository, MyMusicContract.View view) {
        mView = view;
        mRepository = repository;
    }

    @Override
    public void loadOffline() {
        mRepository.getOfflineTracks(new TrackDataSource.DataCallback<Track>() {
            @Override
            public void onSuccess(List<Track> datas) {
                mView.onSuccess(datas);
            }

            @Override
            public void onFail(String mesage) {
                mView.onFailure(mesage);
            }
        });
    }
}
