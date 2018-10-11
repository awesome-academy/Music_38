package com.framgia.vhlee.musicplus.ui;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.framgia.vhlee.musicplus.R;
import com.framgia.vhlee.musicplus.data.model.Track;
import com.framgia.vhlee.musicplus.service.MyService;
import com.framgia.vhlee.musicplus.ui.play.PlayActivity;
import com.framgia.vhlee.musicplus.util.Constants;

import java.util.List;

public class MiniPlayerClass implements View.OnClickListener {
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
                if (mService.isPlaying()) {
                    mService.requestPause();
                    mPlayImage.setImageResource(R.drawable.play_button);
                } else {
                    mService.requestStart();
                    mPlayImage.setImageResource(R.drawable.pause);
                }
                break;
            default:
                clickMiniPlayer();
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
        mPlayImage.setVisibility(View.VISIBLE);
        mPlayImage.setImageResource(R.drawable.pause);
    }

    public void startLoading(int index) {
        mPlayImage.setVisibility(View.INVISIBLE);
        mMiniPlayer.setVisibility(View.VISIBLE);
        Track track = mService.getTracks().get(index);
        mTrackSinger.setText(track.getArtist());
        mTrackName.setText(track.getTitle());
        if (!mActivity.isDestroyed()) {
            Glide.with(mActivity)
                    .load(track.getArtworkUrl())
                    .into(mTrackImage);
        }
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
            mPlayImage.setImageResource(R.drawable.pause);
        } else {
            mPlayImage.setImageResource(R.drawable.play_button);
        }
    }

    public void pause() {
        mPlayImage.setImageResource(R.drawable.play_button);
    }

    private void clickMiniPlayer() {
        mActivity.startActivity(new Intent(mActivity, PlayActivity.class));
    }
}
