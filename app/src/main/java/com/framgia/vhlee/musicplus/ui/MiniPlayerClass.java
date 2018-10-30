package com.framgia.vhlee.musicplus.ui;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.framgia.vhlee.musicplus.R;
import com.framgia.vhlee.musicplus.data.model.Track;
import com.framgia.vhlee.musicplus.mediaplayer.PlayMusicInterface;
import com.framgia.vhlee.musicplus.service.MyService;
import com.framgia.vhlee.musicplus.ui.play.PlayActivity;
import com.framgia.vhlee.musicplus.util.Constants;

import java.util.List;

public class MiniPlayerClass implements View.OnClickListener {
    private static final String TRANSITION_ARTWORK = "fromMini";
    private static final int TOTAL = 1;
    private ProgressBar mProgressWaiting;
    private ImageView mPlayImage;
    private View mMiniPlayer;
    private TextView mTrackName;
    private TextView mTrackSinger;
    private ImageView mNextTrack;
    private ImageView mPreviousTrack;
    private ImageView mTrackImage;
    private MyService mService;
    private Activity mActivity;

    public MiniPlayerClass(Activity activity) {
        mActivity = activity;
        initView();
    }

    private void initView() {
        mMiniPlayer = mActivity.findViewById(R.id.mini_player);
        mProgressWaiting = mActivity.findViewById(R.id.progress_waiting);
        mPlayImage = mActivity.findViewById(R.id.image_play_song);
        mTrackName = mActivity.findViewById(R.id.text_song_name);
        mTrackSinger = mActivity.findViewById(R.id.text_singer_name);
        mNextTrack = mActivity.findViewById(R.id.image_next_song);
        mPreviousTrack = mActivity.findViewById(R.id.image_previous_song);
        mTrackImage = mActivity.findViewById(R.id.image_track);
        mMiniPlayer.setOnClickListener(this);
        mPlayImage.setOnClickListener(this);
        mNextTrack.setOnClickListener(this);
        mPreviousTrack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_next_song:
                mService.requestChangeSong(Constants.NEXT_SONG);
                break;
            case R.id.image_previous_song:
                mService.requestChangeSong(Constants.PREVIOUS_SONG);
                break;
            case R.id.image_play_song:
                playSong();
                break;
            default:
                switchPlay();
                break;
        }
    }

    public ImageView getPlayImage() {
        return mPlayImage;
    }

    public View getMiniPlayer() {
        return mMiniPlayer;
    }

    public TextView getTrackName() {
        return mTrackName;
    }

    public TextView getTrackSinger() {
        return mTrackSinger;
    }

    public ImageView getNextTrack() {
        return mNextTrack;
    }

    public ImageView getPreviousTrack() {
        return mPreviousTrack;
    }

    public ImageView getTrackImage() {
        return mTrackImage;
    }

    public MyService getService() {
        return mService;
    }

    public MiniPlayerClass setService(MyService service) {
        mService = service;
        return this;
    }

    public void loadingSuccess() {
        mMiniPlayer.setVisibility(View.VISIBLE);
        mPlayImage.setClickable(true);
        mProgressWaiting.setVisibility(View.GONE);
        mPlayImage.setImageResource(R.drawable.ic_pause);
        mMiniPlayer.setClickable(true);
    }

    public void startLoading(int index) {
        mPlayImage.setClickable(false);
        mProgressWaiting.setVisibility(View.VISIBLE);
        mMiniPlayer.setVisibility(View.VISIBLE);
        Track track = mService.getTracks().get(index);
        mTrackSinger.setText(track.getArtist());
        mTrackName.setText(track.getTitle());
        if (!mActivity.isDestroyed()) {
            Glide.with(mActivity)
                    .load(track.getArtworkUrl())
                    .into(mTrackImage);
        }
        mMiniPlayer.setClickable(false);
    }

    public void update() {
        List<Track> tracks = mService.getTracks();
        int index = mService.getSong();
        mMiniPlayer.setVisibility(View.VISIBLE);
        mTrackName.setText(tracks.get(index).getTitle());
        mTrackSinger.setText(tracks.get(index).getArtist());
        if (!mActivity.isDestroyed()) {
            Glide.with(mActivity)
                    .load(tracks.get(index).getArtworkUrl())
                    .into(mTrackImage);
        }
        if (mService.isPlaying()) {
            mPlayImage.setImageResource(R.drawable.ic_pause);
            return;
        }
        mPlayImage.setImageResource(R.drawable.ic_play);
        int status = mService.getMediaPlayerManager().getStatus();
        if (status == PlayMusicInterface.StatusPlayerType.STOPPED
                || status == PlayMusicInterface.StatusPlayerType.PAUSED) {
            setClickablePlayImage(true);
            return;
        }
        setClickablePlayImage(false);
    }

    public void pause() {
        mPlayImage.setImageResource(R.drawable.ic_play);
    }

    public void stop() {
        mPlayImage.setImageResource(R.drawable.ic_play);
    }

    private void playSong() {
        if (mService.isPlaying()) {
            mService.requestPause();
            mPlayImage.setImageResource(R.drawable.ic_play);
            return;
        }
        mPlayImage.setImageResource(R.drawable.ic_pause);
        int mediaStatus = mService.getMediaPlayerManager().getStatus();
        if (mediaStatus == PlayMusicInterface.StatusPlayerType.STOPPED) {
            mService.requestPrepareAsync();
            return;
        }
        mService.requestStart();
    }

    private void setClickablePlayImage(boolean isVisible) {
        if (isVisible) {
            mPlayImage.setClickable(true);
            return;
        }
        mPlayImage.setClickable(false);
    }

    private void switchPlay() {
        Intent intent = new Intent(mActivity, PlayActivity.class);
        Pair[] pairs = new Pair[TOTAL];
        pairs[0] = new Pair<View, String>(mTrackImage, TRANSITION_ARTWORK);
        ActivityOptions options =
                ActivityOptions.makeSceneTransitionAnimation(mActivity, pairs);
        mActivity.startActivity(intent, options.toBundle());
    }
}
