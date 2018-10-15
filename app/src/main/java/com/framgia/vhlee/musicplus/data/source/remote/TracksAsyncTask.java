package com.framgia.vhlee.musicplus.data.source.remote;

import com.framgia.vhlee.musicplus.data.model.Track;
import com.framgia.vhlee.musicplus.data.source.TrackDataSource;
import com.framgia.vhlee.musicplus.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TracksAsyncTask extends BaseAsyncTask<Track> {

    public TracksAsyncTask(TrackDataSource.DataCallback<Track> callback) {
        super(callback);
    }

    @Override
    public List<Track> convertJson(String jsonString) {
        List<Track> tracks = new ArrayList<>();
        try {
            JSONObject result = new JSONObject(jsonString);
            JSONArray collection = result.getJSONArray(Constants.Track.COLLECTION);
            for (int i = 0; i < collection.length(); i++) {
                JSONObject trackInfo = collection.getJSONObject(i);
                JSONObject track = trackInfo.getJSONObject(Constants.Track.TRACK);
                JSONObject publisher = track.getJSONObject(Constants.Track.PUBLISHER);
                int id = track.getInt(Constants.Track.ID);
                String title = track.getString(Constants.Track.TITLE);
                String artworkUrl = track.getString(Constants.Track.ARTWORK_URL);
                String artist = publisher.getString(Constants.Track.ARTIST);
                Track trackObject = new Track(id, title, artist);
                trackObject.setArtworkUrl(artworkUrl);
                tracks.add(trackObject);
            }
        } catch (JSONException e) {
            mException = e;
        }
        return tracks;
    }
}
