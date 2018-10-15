package com.framgia.vhlee.musicplus.data.repository;

import com.framgia.vhlee.musicplus.data.model.Track;
import com.framgia.vhlee.musicplus.data.source.TrackDataSource;
import com.framgia.vhlee.musicplus.data.source.remote.TrackRemoteDataSource;

public class TrackDataRepository implements TrackDataSource.Local, TrackDataSource.Remote {

    private static TrackDataRepository sInstance;
    private TrackDataSource.Remote mRemoteDataSource;

    private TrackDataRepository() {
        mRemoteDataSource = TrackRemoteDataSource.getsInstance();
    }

    public static synchronized TrackDataRepository getsInstance() {
        if (sInstance == null) {
            sInstance = new TrackDataRepository();
        }
        return sInstance;
    }

    @Override
    public void getTracks(String api, DataCallback<Track> callback) {
        mRemoteDataSource.getTracks(api, callback);
    }
}
