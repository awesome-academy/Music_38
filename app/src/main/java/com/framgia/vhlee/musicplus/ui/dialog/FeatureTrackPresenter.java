package com.framgia.vhlee.musicplus.ui.dialog;

import com.framgia.vhlee.musicplus.data.model.Track;
import com.framgia.vhlee.musicplus.data.repository.TrackRepository;
import com.framgia.vhlee.musicplus.data.source.TrackDataSource;

import java.util.List;

public class FeatureTrackPresenter implements FeatureTrackContract.Presenter {
    private FeatureTrackContract.View mView;
    private TrackRepository mRepository;

    public FeatureTrackPresenter(TrackRepository repository, FeatureTrackContract.View view) {
        mView = view;
        mRepository = repository;
    }

    @Override
    public void addFavariteTrack(final Track track) {
        mRepository.addFavariteTrack(track, new TrackDataSource.DataCallback<Boolean>() {
            @Override
            public void onSuccess(List<Boolean> datas) {
                boolean isSuccess = datas.get(0);
                if (isSuccess) {
                    mView.onAddTracksSuccess();
                    return;
                }
                mView.onFail();
            }

            @Override
            public void onFail(String mesage) {
                mView.existTrackInFavorites(mesage);
            }
        });
    }
}
