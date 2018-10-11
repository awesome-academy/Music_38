package com.framgia.vhlee.musicplus.data.source.remote;

import com.framgia.vhlee.musicplus.data.model.Track;
import com.framgia.vhlee.musicplus.data.source.TrackDataSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LoadTrackAsyncTask extends BaseAsyncTask<Track> {
    private static final String KEY_COLLECTION = "collection";
    private static final String KEY_TRACK_TITLE = "title";
    private static final String KEY_TRACK = "track";
    private static final String KEY_TRACK_IMAGE = "artwork_url";
    private static final String KEY_TRACK_TD = "id";
    private static final String KEY_USER = "user";
    private static final String KEY_USER_NAME = "username";

    public LoadTrackAsyncTask(TrackDataSource.DataCallback<Track> callback) {
        super(callback);
    }

    @Override
    public List<Track> convertJson(String jsonString) {
        List<Track> tracks = new ArrayList<>();
        if (jsonString == null) return tracks;
        try {
            JSONObject root = new JSONObject(jsonString);
            JSONArray collection = root.getJSONArray(KEY_COLLECTION);
            for (int i = 0; i < collection.length(); i++) {
                JSONObject trackJsonObject = collection.getJSONObject(i);
                JSONObject trackObject = trackJsonObject.getJSONObject(KEY_TRACK);
                String image_url = trackObject.getString(KEY_TRACK_IMAGE);
                long id = trackObject.getLong(KEY_TRACK_TD);
                String title = trackObject.getString(KEY_TRACK_TITLE);
                String artist = trackObject.getJSONObject(KEY_USER).getString(KEY_USER_NAME);
                Track track = new Track(id, title, artist);
                track.setArtworkUrl(image_url);
                tracks.add(track);
            }
        } catch (JSONException e) {
            this.mException = e;
        }
        return tracks;
    }
}
