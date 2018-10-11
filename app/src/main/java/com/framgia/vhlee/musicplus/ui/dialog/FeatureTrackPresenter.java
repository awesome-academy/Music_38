package com.framgia.vhlee.musicplus.ui.dialog;

import android.content.Context;

import com.framgia.vhlee.musicplus.data.model.Track;
import com.framgia.vhlee.musicplus.data.repository.TrackRepository;
import com.framgia.vhlee.musicplus.data.source.TrackDataSource;
import com.framgia.vhlee.musicplus.data.source.local.TrackLocalDataSource;
import com.framgia.vhlee.musicplus.data.source.remote.TrackRemoteDataSource;

import java.util.List;

public class FeatureTrackPresenter implements FeatureTrackContract.Presenter {
    private FeatureTrackContract.View mView;
    private TrackRepository mRepository;

    public FeatureTrackPresenter(Context context, FeatureTrackContract.View view) {
        mView = view;
        mRepository = TrackRepository.getsInstance(TrackRemoteDataSource.getsInstance(),
                TrackLocalDataSource.getInstance(context));
    }

    @Override
    public void addFavariteTrack(Track track) {
        mRepository.addFavariteTrack(track, new TrackDataSource.DataCallback<String>() {
            @Override
            public void onSuccess(List<String> datas) {
                mView.onAddTracksSuccess(datas);
            }

            @Override
            public void onFail(String mesage) {
                mView.onFail(mesage);
            }
        });
    }
}
