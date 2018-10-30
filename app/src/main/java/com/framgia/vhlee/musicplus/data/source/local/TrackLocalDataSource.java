package com.framgia.vhlee.musicplus.data.source.local;

import android.content.Context;

import com.framgia.vhlee.musicplus.data.model.Track;
import com.framgia.vhlee.musicplus.data.source.TrackDataSource;
import com.framgia.vhlee.musicplus.util.MySharedPreferences;

import java.util.List;

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
    public void getOfflineTracks(TrackDataSource.DataCallback<Track> callback) {
        new MusicStorage(sContext, callback).execute();
    }

    @Override
    public void getFavotiteTracks(TrackDataSource.DataCallback<Track> callback) {
        sDbHelper.getTracks(callback);
    }

    @Override
    public void addFavariteTrack(Track track, TrackDataSource.DataCallback<Boolean> callback) {
        if (sDbHelper.canAddTrack(track)) {
            sDbHelper.putTrack(track, callback);
            return;
        }
        callback.onFail(EXIST_TRACK);
    }

    @Override
    public void deleteFavoriteTrack(Track track, TrackDataSource.DataCallback<Boolean> callback) {
        sDbHelper.deleteTrack(track, callback);
    }

    @Override
    public void getRecentTrack(TrackDataSource.DataCallback<Long> callback) {
        MySharedPreferences mPreferences = new MySharedPreferences(sContext);
        List<Long> idRecentTracks = mPreferences.getData();
        callback.onSuccess(idRecentTracks);
    }
}
