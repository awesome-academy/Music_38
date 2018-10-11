package com.framgia.vhlee.musicplus.data.repository;

import android.content.Context;

import com.framgia.vhlee.musicplus.data.model.Track;
import com.framgia.vhlee.musicplus.data.source.TrackDataSource;
import com.framgia.vhlee.musicplus.data.source.local.TrackLocalDataSource;
import com.framgia.vhlee.musicplus.data.source.remote.TrackRemoteDataSource;

public class TrackDataRepository implements TrackDataSource.Local, TrackDataSource.Remote {

    private static TrackDataRepository sInstance;
    private TrackDataSource.Remote mRemoteDataSource;
    private TrackDataSource.Local mLocalDataSource;

    private TrackDataRepository() {
        mRemoteDataSource = TrackRemoteDataSource.getsInstance();
        mLocalDataSource = TrackLocalDataSource.getInstance();
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

    @Override
    public void searchTracks(String api, DataCallback<Track> callback) {
        mRemoteDataSource.searchTracks(api, callback);
    }

    @Override
    public void loadOffline(Context context, DataCallback<Track> callback) {
        mLocalDataSource.loadOffline(context, callback);
    }

    @Override
    public void getDetailTrack(String api, DataCallback<Track> callback) {
        mRemoteDataSource.getDetailTrack(api, callback);
    }
}
