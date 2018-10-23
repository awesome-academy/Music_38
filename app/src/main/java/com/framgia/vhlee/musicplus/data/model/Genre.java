package com.framgia.vhlee.musicplus.data.model;

import java.io.Serializable;

public class Genre implements Serializable {
    private String mKey;
    private String mName;
    private int mPhoto;

    public Genre(@GenreKey String key, @GenreName String name, int photo) {
        mKey = key;
        mName = name;
        mPhoto = photo;
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
        mKey = key;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getPhoto() {
        return mPhoto;
    }

    public void setPhoto(int photo) {
        mPhoto = photo;
    }
}
