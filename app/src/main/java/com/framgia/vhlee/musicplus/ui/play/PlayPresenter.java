package com.framgia.vhlee.musicplus.ui.play;

import com.framgia.vhlee.musicplus.data.model.Track;
import com.framgia.vhlee.musicplus.data.repository.TrackRepository;
import com.framgia.vhlee.musicplus.data.source.TrackDataSource;

import java.util.List;

public class PlayPresenter implements PlayContract.Presenter {
    private PlayContract.View mView;
    private TrackRepository mRepository;

    public PlayPresenter(TrackRepository repository, PlayContract.View view) {
        mView = view;
        mRepository = repository;
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
