package com.framgia.vhlee.musicplus.mediaplayer;

public interface PlayMusicInterface {
    void create(int index);

    void prepareAsync();

    void start();

    void pause();

    int getDuration();

    int getCurrrentPosition();

    boolean isPlaying();

    void seek(int possition);

    void loop(boolean isLoop);

    int getSong();

    void changeSong(int i);
}
