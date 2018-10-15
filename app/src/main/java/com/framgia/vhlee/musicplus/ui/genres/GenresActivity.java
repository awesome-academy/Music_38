package com.framgia.vhlee.musicplus.ui.genres;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.framgia.vhlee.musicplus.R;
import com.framgia.vhlee.musicplus.data.model.Genre;
import com.framgia.vhlee.musicplus.data.model.Track;
import com.framgia.vhlee.musicplus.service.MediaRequest;
import com.framgia.vhlee.musicplus.service.MyService;
import com.framgia.vhlee.musicplus.ui.LoadMoreAbstract;
import com.framgia.vhlee.musicplus.ui.adapter.TrackAdapter;
import com.framgia.vhlee.musicplus.ui.dialog.FeatureTrackDialog;
import com.framgia.vhlee.musicplus.util.Constants;
import com.framgia.vhlee.musicplus.util.StringUtil;

import java.util.ArrayList;
import java.util.List;


public class GenresActivity extends LoadMoreAbstract implements GenresContract.View,
        TrackAdapter.OnClickItemSongListener, View.OnClickListener {
    private int mOffset;
    private TrackAdapter mAdapter;
    private List<Track> mTracks;
    private GenresContract.Presenter mPresenter;
    private MyService mService;
    private ImageView mPlayImage;
    private View mMiniPlayer;
    private TextView mTrackName;
    private TextView mTrackSinger;
    private ImageView mNextTrack;
    private ImageView mPreviousTrack;
    private ImageView mTrackImage;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MediaRequest.LOADING:
                    startLoading(msg.arg1);
                    break;
                case MediaRequest.SUCCESS:
                    loadingSuccess();
                    break;
                case MediaRequest.UPDATE_MINI_PLAYER:
                    if (mService.getMediaPlayer() != null) {
                        List<Track> tracks = mService.getTracks();
                        int index = mService.getSong();
                        mMiniPlayer.setVisibility(View.VISIBLE);
                        mTrackName.setText(tracks.get(index).getTitle());
                        mTrackSinger.setText(tracks.get(index).getArtist());
                        Glide.with(GenresActivity.this)
                                .load(tracks.get(index).getArtworkUrl())
                                .into(mTrackImage);
                        if (mService.isPlaying()) {
                            mPlayImage.setImageResource(R.drawable.pause);
                        } else {
                            mPlayImage.setImageResource(R.drawable.play_button);
                        }
                    }
                    break;
                case MediaRequest.FAILURE:
                    Toast.makeText(GenresActivity.this, (String) msg.obj,
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MyService.LocalBinder binder = (MyService.LocalBinder) iBinder;
            mService = binder.getService();
            mService.setUIHandler(mHandler);
            updateMiniPlayer();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            unbindService(mConnection);
        }
    };
    private String mGenreApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genres);
        String title = null;
        initToolbar(title);
        initView();
        initViewLoadMore();
        initData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_genres, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLoadTracksSuccess(List<Track> tracks) {
        if (mOffset == 0) {
            mAdapter.updateTracks(tracks);
        } else {
            mAdapter.addTracks(tracks);
            mProgressBar.setVisibility(View.GONE);
        }
        Intent serviceIntent = getMyServiceIntent(GenresActivity.this);
        if (mService == null) {
            startService(serviceIntent);
        }
        bindService(serviceIntent, mConnection, BIND_AUTO_CREATE);
    }

    @Override
    public void onLoadTracksFail(String message) {
        Toast.makeText(GenresActivity.this, message, Toast.LENGTH_SHORT).show();
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void clickItemSongListener(int position) {
        mService.setTracks(mTracks);
        mService.requestCreate(position);
    }

    @Override
    public void showDialodFeatureTrack(int position) {
        FeatureTrackDialog dialog = new FeatureTrackDialog(GenresActivity.this,
                R.style.Theme_Dialog, mTracks.get(position));
        dialog.show();
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
                clickPlaySong();
            default:
                clickMiniPlayer();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (mService != null) unbindService(mConnection);
        super.onDestroy();
    }

    @Override
    public void loadMoreData() {
        mIsScrolling = false;
        mProgressBar.setVisibility(View.VISIBLE);
        String api = StringUtil.initGenreApi(mGenreApi, ++mOffset);
        mPresenter.getTracks(api);
    }

    @Override
    public void initViewLoadMore() {
        mRecyclerView = findViewById(R.id.recycler_list_tracks);
        mTracks = new ArrayList<>();
        mAdapter = new TrackAdapter(mTracks, this);
        mRecyclerView.setAdapter(mAdapter);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mProgressBar = findViewById(R.id.proress_load_more);
        mProgressBar.setVisibility(View.GONE);
        setLoadMore();
    }

    private void initView() {
        mPresenter = new GenresPresenter(this);
        mMiniPlayer = findViewById(R.id.mini_player);
        mPlayImage = findViewById(R.id.image_play_song);
        mTrackName = findViewById(R.id.text_song_name);
        mTrackSinger = findViewById(R.id.text_singer_name);
        mNextTrack = findViewById(R.id.image_next_song);
        mPreviousTrack = findViewById(R.id.image_previous_song);
        mTrackImage = findViewById(R.id.image_track);
        mMiniPlayer.setOnClickListener(this);
        mPlayImage.setOnClickListener(this);
        mNextTrack.setOnClickListener(this);
        mPreviousTrack.setOnClickListener(this);
    }

    public static Intent getMyServiceIntent(Context context) {
        Intent intent = new Intent(context, MyService.class);
        return intent;
    }

    private void initToolbar(String title) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (title == null || title.isEmpty()) {
            setTitle(getString(R.string.default_toolbar_title));
        } else {
            setTitle(title);
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public static Intent getGenresIntent(Context context, Genre genre) {
        Intent intent = new Intent(context, GenresActivity.class);
        intent.putExtra(Constants.EXTRA_GENRES, genre);
        return intent;
    }

    private void initData() {
        mOffset = 0;
        Intent intent = getIntent();
        Genre genres = (Genre) intent.getSerializableExtra(Constants.EXTRA_GENRES);
        mGenreApi = StringUtil.initGenreApi(genres.getKey(), mOffset);
        mPresenter.getTracks(mGenreApi);
    }

    private void loadingSuccess() {
        mMiniPlayer.setVisibility(View.VISIBLE);
        mPlayImage.setVisibility(View.VISIBLE);
        mPlayImage.setImageResource(R.drawable.pause);
    }

    private void startLoading(int index) {
        mPlayImage.setVisibility(View.INVISIBLE);
        mMiniPlayer.setVisibility(View.VISIBLE);
        Track track = mService.getTracks().get(index);
        mTrackSinger.setText(track.getArtist());
        mTrackName.setText(track.getTitle());
        Glide.with(GenresActivity.this)
                .load(track.getArtworkUrl())
                .into(mTrackImage);
    }

    private void updateMiniPlayer() {
        Message message = new Message();
        message.what = MediaRequest.UPDATE_MINI_PLAYER;
        mHandler.sendMessage(message);
    }

    private void clickPlaySong() {
        if (mService.isPlaying()) {
            mService.pause();
            mPlayImage.setImageResource(R.drawable.play_button);
        } else {
            mService.start();
            mPlayImage.setImageResource(R.drawable.pause);
        }
    }

    private void clickMiniPlayer() {
        Toast.makeText(this, R.string.text_click_mini_player, Toast.LENGTH_SHORT).show();
    }
}
