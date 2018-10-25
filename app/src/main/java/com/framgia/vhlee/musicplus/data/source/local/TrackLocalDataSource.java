package com.framgia.vhlee.musicplus.data.source.local;

import android.content.Context;

import com.framgia.vhlee.musicplus.data.model.Track;
import com.framgia.vhlee.musicplus.data.source.TrackDataSource;

public class TrackLocalDataSource implements TrackDataSource.Local {
    private static TrackLocalDataSource sInstance;

    public TrackLocalDataSource() {
    }

    public static TrackLocalDataSource getInstance() {
        if (sInstance == null) {
            sInstance = new TrackLocalDataSource();
        }
        return sInstance;
    }

    @Override
    public void loadOffline(Context context, TrackDataSource.DataCallback<Track> callback) {
        new MusicStorage(context, callback).execute();
    }
}
