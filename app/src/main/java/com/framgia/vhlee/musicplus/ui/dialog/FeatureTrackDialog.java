package com.framgia.vhlee.musicplus.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.framgia.vhlee.musicplus.R;
import com.framgia.vhlee.musicplus.data.model.Track;

public class FeatureTrackDialog extends BottomSheetDialog implements View.OnClickListener {
    private Context mContext;
    private View mFavoriteView;
    private View mPlaylistView;
    private View mDownloadView;
    private ImageView mTrackImage;
    private TextView mTrackName;
    private TextView mSingerName;
    private Track mTrack;

    public FeatureTrackDialog(@NonNull Context context, int themeResId, Track track) {
        super(context, themeResId);
        mContext = context;
        mTrack = track;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_feature_track);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        initViews();
        updateViews();
    }

    private void updateViews() {
        Glide.with(mContext)
                .load(mTrack.getArtworkUrl())
                .into(mTrackImage);
        mTrackName.setText(mTrack.getTitle());
        mSingerName.setText(mTrack.getArtist());
    }

    private void initViews() {
        mFavoriteView = findViewById(R.id.feature_favorite);
        mPlaylistView = findViewById(R.id.feature_playlist);
        mDownloadView = findViewById(R.id.feature_download);
        mTrackImage = findViewById(R.id.image_track);
        mTrackName = findViewById(R.id.text_song_name);
        mSingerName = findViewById(R.id.text_singer_name);
        mFavoriteView.setOnClickListener(this);
        mPlaylistView.setOnClickListener(this);
        mDownloadView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.feature_download:
                download();
                break;
            case R.id.feature_favorite:
                addToFavorites();
                break;
            case R.id.feature_playlist:
                addToPlayList();
                break;
            default:
                break;
        }
    }

    private void download() {
        String download = mContext.getResources().getString(R.string.text_download);
        Toast.makeText(mContext, download, Toast.LENGTH_SHORT).show();
    }

    private void addToPlayList() {
        String playList = mContext.getResources().getString(R.string.text_playlist);
        Toast.makeText(mContext, playList, Toast.LENGTH_SHORT).show();
    }

    private void addToFavorites() {
        String favorites = mContext.getResources().getString(R.string.text_favorites);
        Toast.makeText(mContext, favorites, Toast.LENGTH_SHORT).show();
    }
}
