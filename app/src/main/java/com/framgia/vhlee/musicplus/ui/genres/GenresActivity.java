package com.framgia.vhlee.musicplus.ui.genres;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.framgia.vhlee.musicplus.R;
import com.framgia.vhlee.musicplus.data.model.Genre;
import com.framgia.vhlee.musicplus.data.model.Track;
import com.framgia.vhlee.musicplus.service.MediaRequest;
import com.framgia.vhlee.musicplus.service.MyService;
import com.framgia.vhlee.musicplus.ui.LoadMoreAbstract;
import com.framgia.vhlee.musicplus.ui.MiniPlayerClass;
import com.framgia.vhlee.musicplus.ui.adapter.TrackAdapter;
import com.framgia.vhlee.musicplus.ui.dialog.FeatureTrackDialog;
import com.framgia.vhlee.musicplus.ui.search.SearchActivity;
import com.framgia.vhlee.musicplus.util.Constants;
import com.framgia.vhlee.musicplus.util.StringUtil;

import java.util.ArrayList;
import java.util.List;


public class GenresActivity extends LoadMoreAbstract implements GenresContract.View,
        TrackAdapter.OnClickItemSongListener, View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener {
    private int mOffset;
    private TrackAdapter mAdapter;
    private List<Track> mTracks;
    private GenresContract.Presenter mPresenter;
    private MyService mService;
    private MiniPlayerClass mMiniPlayerClass;
    private String mGenreKey;
    private String mGenreApi;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MediaRequest.LOADING:
                    mMiniPlayerClass.startLoading(msg.arg1);
                    break;
                case MediaRequest.SUCCESS:
                    mMiniPlayerClass.loadingSuccess();
                    break;
                case MediaRequest.UPDATE_MINI_PLAYER:
                    if (mService != null && mService.getMediaPlayer() != null) {
                        mMiniPlayerClass.update();
                    }
                    break;
                case MediaRequest.FAILURE:
                    Toast.makeText(GenresActivity.this, (String) msg.obj,
                            Toast.LENGTH_SHORT).show();
                    break;
                case MediaRequest.PAUSED:
                    mMiniPlayerClass.pause();
                    break;
                case MediaRequest.STOPPED:
                    mMiniPlayerClass.stop();
                    break;
                default:
                    break;
            }
        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MyService.LocalBinder binder = (MyService.LocalBinder) iBinder;
            mService = binder.getService();
            MyService.setUIHandler(mHandler);
            mMiniPlayerClass.setService(mService);
            updateMiniPlayer();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            unbindService(mConnection);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genres);
        initToolbar();
        initView();
        initViewLoadMore();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyService.setUIHandler(mHandler);
        updateMiniPlayer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_genres, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                startActivity(new Intent(this, SearchActivity.class));
                break;
            default:
                super.onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public void onLoadTracksSuccess(List<Track> tracks) {
        if (mOffset == 0) {
            mAdapter.updateTracks(tracks);
            mSwipeRefreshLayout.setRefreshing(false);
        } else {
            mAdapter.addTracks(tracks);
            mProgressBar.setVisibility(View.GONE);
        }
        Intent serviceIntent = MyService.getMyServiceIntent(GenresActivity.this);
        if (mService == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }
        }
        bindService(serviceIntent, mConnection, BIND_AUTO_CREATE);
    }

    @Override
    public void onLoadTracksFail(String message) {
        Toast.makeText(GenresActivity.this, message, Toast.LENGTH_SHORT).show();
        mProgressBar.setVisibility(View.GONE);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void clickItemSongListener(int position) {
        mService.setTracks(mTracks);
        mService.requestCreate(position);
    }


    @Override
    public void showDialodFeatureTrack(int position) {
        FeatureTrackDialog dialog = new FeatureTrackDialog(GenresActivity.this,
                R.style.ThemeDialog, mTracks.get(position));
        dialog.show();
    }

    @Override
    public void onClick(View view) {
        mMiniPlayerClass.onClick(view);
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
        String api = StringUtil.initGenreApi(mGenreKey, ++mOffset);
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

    @Override
    public void onRefresh() {
        if (mGenreKey != null && !mGenreKey.isEmpty()) {
            mOffset = 0;
            String api = StringUtil.initGenreApi(mGenreKey, mOffset);
            mPresenter.getTracks(api);
        }
    }

    private void initView() {
        mPresenter = new GenresPresenter(this);
        mMiniPlayerClass = new MiniPlayerClass(this);
        mSwipeRefreshLayout = findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    private void initToolbar() {
        Intent intent = getIntent();
        Genre genres = (Genre) intent.getSerializableExtra(Constants.EXTRA_GENRES);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle(genres.getName());
        ImageView imageCover = findViewById(R.id.image_collapse);
        imageCover.setImageResource(genres.getPhoto());
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
        mGenreKey = genres.getKey();
        mGenreApi = StringUtil.initGenreApi(mGenreKey, mOffset);
        mPresenter.getTracks(mGenreApi);
    }

    private void updateMiniPlayer() {
        Message message = new Message();
        message.what = MediaRequest.UPDATE_MINI_PLAYER;
        mHandler.sendMessage(message);
    }
}
