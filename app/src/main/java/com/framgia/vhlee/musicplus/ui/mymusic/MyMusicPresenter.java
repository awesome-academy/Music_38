package com.framgia.vhlee.musicplus.ui.mymusic;

import android.content.Context;

import com.framgia.vhlee.musicplus.data.model.Track;
import com.framgia.vhlee.musicplus.data.repository.TrackRepository;
import com.framgia.vhlee.musicplus.data.source.TrackDataSource;
import com.framgia.vhlee.musicplus.data.source.local.TrackLocalDataSource;
import com.framgia.vhlee.musicplus.data.source.remote.TrackRemoteDataSource;

import java.util.List;

public class MyMusicPresenter implements MyMusicContract.Presenter {
    private MyMusicContract.View mView;
    private TrackRepository mRepository;

    public MyMusicPresenter(Context context, MyMusicContract.View view) {
        mView = view;
        mRepository = TrackRepository.getsInstance(TrackRemoteDataSource.getsInstance(),
                TrackLocalDataSource.getInstance(context));
    }

    @Override
    public void loadOffline() {
        mRepository.loadOffline(new TrackDataSource.DataCallback<Track>() {
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
