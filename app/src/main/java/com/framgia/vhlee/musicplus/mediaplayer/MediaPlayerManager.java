package com.framgia.vhlee.musicplus.mediaplayer;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import com.framgia.vhlee.musicplus.data.model.Track;
import com.framgia.vhlee.musicplus.ui.adapter.MediaPlayerListener;
import com.framgia.vhlee.musicplus.util.Constants;

import java.io.IOException;
import java.util.List;

public class MediaPlayerManager extends MediaPlayerSetting
        implements PlayMusicInterface, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener {

    private static final int NUMBER_1 = 1;
    private static MediaPlayerManager sInstance;
    private Context mContext;
    private MediaPlayer mMediaPlayer;
    private List<Track> mTracks;
    private int mCurrentIndex;
    private int mStatus;
    private OnLoadingTrackListener mListener;
    private MediaPlayerListener mPlayerListener;

    private MediaPlayerManager(Context context, OnLoadingTrackListener listener) {
        mContext = context;
        mListener = listener;
        mLoopType = LoopType.NONE;
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
        mCurrentIndex = index;
        Track track = mTracks.get(mCurrentIndex);
        mListener.onStartLoading(index);
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mStatus = StatusPlayerType.IDLE;
        } else {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnCompletionListener(this);
        }
        if (!mTracks.isEmpty() && mCurrentIndex >= 0) {
            if (track.isOffline()) initOffline(track);
            else initOnline(track);
            mMediaPlayer.setOnCompletionListener(this);
        }
    }

    private void initOnline(Track track) {
        Uri uri = Uri.parse(mTracks.get(mCurrentIndex).getStreamUrl());
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mMediaPlayer.setDataSource(mContext, uri);
            mStatus = StatusPlayerType.INITIALIZED;
            prepareAsync();
        } catch (IOException e) {
            mListener.onLoadingFail(e.getMessage());
        }
    }

    private void initOffline(Track track) {
        mMediaPlayer = MediaPlayer.create(mContext,
                Uri.parse(track.getDownloadUrl()));
        start();
    }

    @Override
    public void prepareAsync() {
        if (mMediaPlayer != null) {
            mMediaPlayer.prepareAsync();
            mStatus = StatusPlayerType.PREPARING;
            mMediaPlayer.setOnPreparedListener(this);
        }
    }

    @Override
    public void start() {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
            mStatus = StatusPlayerType.STARTED;
            mListener.onLoadingSuccess();
        }
    }

    @Override
    public void pause() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
            mStatus = StatusPlayerType.PAUSED;
            mListener.onTrackPaused();
        }
    }

    @Override
    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mStatus = StatusPlayerType.STOPPED;
            mListener.onTrackStopped();
        }
    }

    @Override
    public int getDuration() {
        if (mMediaPlayer != null && mStatus != StatusPlayerType.IDLE) {
            return mMediaPlayer.getDuration();
        }
        return 0;
    }

    @Override
    public int getCurrrentPosition() {
        if (mMediaPlayer != null && mStatus != StatusPlayerType.IDLE) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
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
        switch (mLoopType) {
            case LoopType.NONE:
                if (mCurrentIndex == mTracks.size() - NUMBER_1
                        && mStatus != StatusPlayerType.STOPPED) {
                    stop();
                } else {
                    changeSong(Constants.NEXT_SONG);
                }
                break;
            case LoopType.ALL:
                changeSong(Constants.NEXT_SONG);
                break;
            case LoopType.ONE:
                start();
                break;
            default:
                break;
        }
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

    public void setPlayerListener(MediaPlayerListener listener) {
        mPlayerListener = listener;
    }

    public int getStatus() {
        return mStatus;
    }

    public MediaPlayerManager setStatus(int status) {
        this.mStatus = status;
        return this;
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

        void onTrackStopped();
    }
}
