package com.framgia.vhlee.musicplus.data.model;

import java.util.ArrayList;
import java.util.List;

public class Playlist {
    private int mId;
    private String mName;
    private String mDescription;
    private int mTrackCount;
    private List<Integer> mTrackIds;
    private String mArtworkUrl;

    public Playlist(int id, int trackCount) {
        mId = id;
        mTrackCount = trackCount;
        mTrackIds = new ArrayList<>();
    }

    public int getId() {
        return mId;
    }

    public Playlist setId(int id) {
        mId = id;
        return this;
    }

    public String getName() {
        return mName;
    }

    public Playlist setName(String name) {
        mName = name;
        return this;
    }

    public String getDescription() {
        return mDescription;
    }

    public Playlist setDescription(String description) {
        mDescription = description;
        return this;
    }

    public int getTrackCount() {
        return mTrackCount;
    }

    public Playlist setTrackCount(int trackCount) {
        mTrackCount = trackCount;
        return this;
    }

    public List<Integer> getTrackIds() {
        return mTrackIds;
    }

    public void addTRackId(int id) {
        mTrackIds.add(id);
    }

    public String getArtworkUrl() {
        return mArtworkUrl;
    }

    public Playlist setArtworkUrl(String artworkUrl) {
        mArtworkUrl = artworkUrl;
        return this;
    }
}
