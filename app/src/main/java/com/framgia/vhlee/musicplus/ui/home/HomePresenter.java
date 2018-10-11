package com.framgia.vhlee.musicplus.ui.home;

import android.content.Context;

import com.framgia.vhlee.musicplus.data.model.Track;
import com.framgia.vhlee.musicplus.data.repository.TrackRepository;
import com.framgia.vhlee.musicplus.data.source.TrackDataSource;
import com.framgia.vhlee.musicplus.data.source.local.TrackLocalDataSource;
import com.framgia.vhlee.musicplus.data.source.remote.TrackRemoteDataSource;

import java.util.List;

public class HomePresenter implements HomeContract.Presenter {
    private HomeContract.View mView;
    private TrackRepository mRepository;

    public HomePresenter(Context context, HomeContract.View view) {
        mView = view;
        mRepository = TrackRepository.getsInstance(TrackRemoteDataSource.getsInstance(),
                TrackLocalDataSource.getInstance(context));
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
