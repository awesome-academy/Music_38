package com.framgia.vhlee.musicplus.ui.genres;

import com.framgia.vhlee.musicplus.data.model.Track;
import com.framgia.vhlee.musicplus.data.repository.TrackRepository;
import com.framgia.vhlee.musicplus.data.source.TrackDataSource;

import java.util.List;

public class GenresPresenter implements GenresContract.Presenter {
    private GenresContract.View mView;
    private TrackRepository mRepository;

    public GenresPresenter(TrackRepository repository, GenresContract.View view) {
        mView = view;
        mRepository = repository;
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
