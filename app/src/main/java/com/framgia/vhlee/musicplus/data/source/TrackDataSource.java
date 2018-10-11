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
        void loadOffline(DataCallback<Track> callback);

        void getFavotiteTracks(DataCallback<Long> callback);

        void addFavariteTrack(Track track, DataCallback<String> callback);

        void deleteFavoriteTrack(Track track, DataCallback<String> callback);
    }

    interface Remote extends TrackDataSource {
        void getTracks(String api, DataCallback<Track> callback);

        void searchTracks(String api, DataCallback<Track> callback);

        void getDetailTrack(String api, DataCallback<Track> callback);
    }
}
