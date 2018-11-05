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

    public static synchronized TrackRepository getInstance(TrackDataSource.Remote remoteDataSource,
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
    public void getOfflineTracks(DataCallback<Track> callback) {
        mLocalDataSource.getOfflineTracks(callback);
    }

    @Override
    public void getFavotiteTracks(DataCallback<Track> callback) {
        mLocalDataSource.getFavotiteTracks(callback);
    }

    @Override
    public void getRecentTrack(DataCallback<Long> callback) {
        mLocalDataSource.getRecentTrack(callback);
    }

    @Override
    public void addFavariteTrack(Track track, DataCallback callback) {
        mLocalDataSource.addFavariteTrack(track, callback);
    }

    @Override
    public void deleteFavoriteTrack(Track track, DataCallback callback) {
        mLocalDataSource.deleteFavoriteTrack(track, callback);
    }

    @Override
    public void getDetailTrack(String api, DataCallback<Track> callback) {
        mRemoteDataSource.getDetailTrack(api, callback);
    }
}
