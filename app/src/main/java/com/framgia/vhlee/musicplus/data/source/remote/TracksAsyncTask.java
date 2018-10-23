package com.framgia.vhlee.musicplus.data.source.remote;

import com.framgia.vhlee.musicplus.data.model.Track;
import com.framgia.vhlee.musicplus.data.source.TrackDataSource;
import com.framgia.vhlee.musicplus.util.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TracksAsyncTask extends BaseAsyncTask<Track> {
    private static final String ARTWORK_URL = "artwork_url";
    private static final String COLLECTION = "collection";
    public static final String ID = "id";
    private static final String KEY_USER = "user";
    private static final String KEY_USER_NAME = "username";
    private static final String TITLE = "title";
    private static final String TRACK = "track";
    private static final String DOWNLOADABLE = "downloadable";
    private static final String DOWNLOAD_URL = "download_url";

    public TracksAsyncTask(TrackDataSource.DataCallback<Track> callback) {
        super(callback);
    }

    @Override
    public List<Track> convertJson(String jsonString) {
        List<Track> tracks = new ArrayList<>();
        try {
            JSONObject result = new JSONObject(jsonString);
            JSONArray collection = result.getJSONArray(COLLECTION);
            for (int i = 0; i < collection.length(); i++) {
                JSONObject trackInfo = collection.getJSONObject(i);
                JSONObject track = trackInfo.getJSONObject(TRACK);
                int id = track.getInt(ID);
                String title = track.getString(TITLE);
                String artworkUrl = track.getString(ARTWORK_URL);
                String artist = track.getJSONObject(KEY_USER)
                        .getString(KEY_USER_NAME);
                boolean isDownloadable = track.getBoolean(DOWNLOADABLE);
                String downloadUrl = null;
                if (isDownloadable) {
                    downloadUrl = StringUtil.initDownloadApi(track.getString(DOWNLOAD_URL));
                }
                Track trackObject = new Track(id, title, artist);
                trackObject.setArtworkUrl(artworkUrl)
                        .setDownloadable(isDownloadable)
                        .setDownloadUrl(downloadUrl);
                tracks.add(trackObject);
            }
        } catch (JSONException e) {
            mException = e;
        }
        return tracks;
    }
}
