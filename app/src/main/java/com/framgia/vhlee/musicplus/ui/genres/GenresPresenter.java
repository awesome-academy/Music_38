package com.framgia.vhlee.musicplus.ui.genres;

import android.content.Context;

import com.framgia.vhlee.musicplus.data.model.Track;
import com.framgia.vhlee.musicplus.data.repository.TrackRepository;
import com.framgia.vhlee.musicplus.data.source.TrackDataSource;
import com.framgia.vhlee.musicplus.data.source.local.TrackLocalDataSource;
import com.framgia.vhlee.musicplus.data.source.remote.TrackRemoteDataSource;

import java.util.List;

public class GenresPresenter implements GenresContract.Presenter {
    private GenresContract.View mView;
    private TrackRepository mRepository;

    public GenresPresenter(Context context, GenresContract.View view) {
        mView = view;
        mRepository = TrackRepository.getsInstance(TrackRemoteDataSource.getsInstance(),
                TrackLocalDataSource.getInstance(context));
    }

    @Override
    public void getTracks(String api) {
        mRepository.getTracks(api, new TrackDataSource.DataCallback<Track>() {
            @Override
            public void onSuccess(List<Track> tracks) {
                mView.onLoadTracksSuccess(tracks);
            }

            @Override
            public void onFail(String mesage) {
                mView.onLoadTracksFail(mesage);
            }
        });
    }
}
