package com.framgia.vhlee.musicplus.data.repository;

import com.framgia.vhlee.musicplus.data.model.Track;
import com.framgia.vhlee.musicplus.data.source.TrackDataSource;

public class TrackRepository implements TrackDataSource.Local, TrackDataSource.Remote {
    private static TrackRepository sInstance;
    private TrackDataSource.Remote mRemoteDataSource;
    private TrackDataSource.Local mLocalDataSource;

    private TrackRepository(TrackDataSource.Remote remoteDataSource,
                            TrackDataSource.Local localDataSource) {
        mRemoteDataSource = remoteDataSource;
        mLocalDataSource = localDataSource;
    }

    public static synchronized TrackRepository getsInstance(TrackDataSource.Remote remoteDataSource,
                                                            TrackDataSource.Local localDataSource) {
        if (sInstance == null) {
            sInstance = new TrackRepository(remoteDataSource, localDataSource);
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
    public void loadOffline(DataCallback<Track> callback) {
        mLocalDataSource.loadOffline(callback);
    }

    @Override
    public void getFavotiteTracks(DataCallback<Long> callback) {
        mLocalDataSource.getFavotiteTracks(callback);
    }

    @Override
    public void addFavariteTrack(Track track, DataCallback<String> callback) {
        mLocalDataSource.addFavariteTrack(track, callback);
    }

    @Override
    public void deleteFavoriteTrack(Track track, DataCallback<String> callback) {
        mLocalDataSource.deleteFavoriteTrack(track, callback);
    }

    @Override
    public void getDetailTrack(String api, DataCallback<Track> callback) {
        mRemoteDataSource.getDetailTrack(api, callback);
    }
}
