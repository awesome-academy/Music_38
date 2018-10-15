package com.framgia.vhlee.musicplus.data.source.remote;

import com.framgia.vhlee.musicplus.data.model.Track;
import com.framgia.vhlee.musicplus.data.source.TrackDataSource;

public class TrackRemoteDataSource implements TrackDataSource.Remote {

    private static TrackRemoteDataSource sInstance;

    private TrackRemoteDataSource() {
    }

    public static synchronized TrackRemoteDataSource getsInstance() {
        if (sInstance == null) {
            sInstance = new TrackRemoteDataSource();
        }
        return sInstance;
    }

    @Override
    public void getTracks(String api, DataCallback<Track> callback) {

    }
}
