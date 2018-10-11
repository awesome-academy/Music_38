package com.framgia.vhlee.musicplus.mediaplayer;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import com.framgia.vhlee.musicplus.data.model.Track;
import com.framgia.vhlee.musicplus.util.Constants;

import java.io.IOException;
import java.util.List;

public class MediaPlayerManager implements PlayMusicInterface, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener {

    private static final int NUMBER_1 = 1;
    private static MediaPlayerManager sInstance;
    private Context mContext;
    private MediaPlayer mMediaPlayer;
    private List<Track> mTracks;
    private int mCurrentIndex;
    private OnLoadingTrackListener mListener;

    private MediaPlayerManager(Context context, OnLoadingTrackListener listener) {
        mContext = context;
        mListener = listener;
    }

    public static MediaPlayerManager getsInstance(Context context,
                                                  OnLoadingTrackListener listener) {
        if (sInstance == null) {
            sInstance = new MediaPlayerManager(context, listener);
        }
        return sInstance;
    }

    @Override
    public void create(int index) {
        mListener.onStartLoading(index);
        mCurrentIndex = index;
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
        if (!mTracks.isEmpty() && mCurrentIndex >= 0) {
            Uri uri = Uri.parse(mTracks.get(mCurrentIndex).getStreamUrl());
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mMediaPlayer.setDataSource(mContext, uri);
                prepareAsync();
            } catch (IOException e) {
                mListener.onLoadingFail(e.getMessage());
            }
            mMediaPlayer.setOnCompletionListener(this);
        }
    }

    @Override
    public void prepareAsync() {
        if (mMediaPlayer != null) {
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(this);
        }
    }

    @Override
    public void start() {
        if (mMediaPlayer != null) {
            mListener.onLoadingSuccess();
            mMediaPlayer.start();
        }
    }

    @Override
    public void pause() {
        if (mMediaPlayer != null) {
            mListener.onTrackPaused();
            mMediaPlayer.pause();
        }
    }

    @Override
    public int getDuration() {
        return mMediaPlayer != null ? mMediaPlayer.getDuration() : 0;
    }

    @Override
    public int getCurrrentPosition() {
        return mMediaPlayer != null ? mMediaPlayer.getCurrentPosition() : 0;
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer != null ? mMediaPlayer.isPlaying() : false;
    }

    @Override
    public void seek(int possition) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(possition);
        }
    }

    @Override
    public void loop(boolean isLoop) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setLooping(isLoop);
        }
    }

    @Override
    public int getSong() {
        return mMediaPlayer != null ? mCurrentIndex : 0;
    }

    @Override
    public void changeSong(int i) {
        mCurrentIndex += i;
        if (mCurrentIndex >= mTracks.size()) {
            mCurrentIndex = 0;
        } else if (mCurrentIndex < 0) {
            mCurrentIndex = mTracks.size() - NUMBER_1;
        }
        create(mCurrentIndex);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        changeSong(Constants.NEXT_SONG);
    }

    public List<Track> getTracks() {
        return mTracks;
    }

    public MediaPlayerManager setTracks(List<Track> tracks) {
        mTracks = tracks;
        return this;
    }

    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        start();
    }

    public interface OnLoadingTrackListener {
        void onStartLoading(int index);

        void onLoadingFail(String message);

        void onLoadingSuccess();

        void onTrackPaused();
    }
}
