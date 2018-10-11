package com.framgia.vhlee.musicplus.data.source.local;

import android.content.Context;

import com.framgia.vhlee.musicplus.data.model.Track;
import com.framgia.vhlee.musicplus.data.source.TrackDataSource;

public class TrackLocalDataSource implements TrackDataSource.Local {
    private static final String EXIST_TRACK = "Exist track in favorite";
    private static TrackLocalDataSource sInstance;
    private static FavoriteReaderDbHelper sDbHelper;
    private static Context sContext;

    public TrackLocalDataSource(Context context) {
        sDbHelper = new FavoriteReaderDbHelper(context);
    }

    public static TrackLocalDataSource getInstance(Context context) {
        sContext = context;
        if (sInstance == null) {
            sInstance = new TrackLocalDataSource(context);
        }
        return sInstance;
    }

    @Override
    public void loadOffline(TrackDataSource.DataCallback<Track> callback) {
        new MusicStorage(sContext, callback).execute();
    }

    @Override
    public void getFavotiteTracks(TrackDataSource.DataCallback<Long> callback) {
        sDbHelper.getTracks(callback);
    }

    @Override
    public void addFavariteTrack(Track track, TrackDataSource.DataCallback<String> callback) {
        if (sDbHelper.canAddTrack(track)) {
            sDbHelper.putTrack(track, callback);
            return;
        }
        callback.onFail(EXIST_TRACK);
    }

    @Override
    public void deleteFavoriteTrack(Track track, TrackDataSource.DataCallback<String> callback) {
        sDbHelper.deleteTrack(track, callback);
    }
}
