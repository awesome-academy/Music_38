package com.framgia.vhlee.musicplus.data.source;

import android.content.Context;

import com.framgia.vhlee.musicplus.data.model.Track;

import java.util.List;

public interface TrackDataSource {
    interface DataCallback<T> {
        void onSuccess(List<T> datas);

        void onFail(String mesage);
    }

    interface Local {
        void loadOffline(Context context, DataCallback<Track> callback);
    }

    interface Remote extends TrackDataSource {
        void getTracks(String api, DataCallback<Track> callback);
    }
}
