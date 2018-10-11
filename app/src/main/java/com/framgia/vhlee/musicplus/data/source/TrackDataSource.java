package com.framgia.vhlee.musicplus.data.source;

import com.framgia.vhlee.musicplus.data.model.Track;

import java.util.List;

public interface TrackDataSource {
    interface DataCallback<T> {
        void onSuccess(List<T> datas);

        void onFail(String mesage);
    }

    interface Local {

    }

    interface Remote extends TrackDataSource {
        void getTracks(String api, DataCallback<Track> callback);
    }
}
