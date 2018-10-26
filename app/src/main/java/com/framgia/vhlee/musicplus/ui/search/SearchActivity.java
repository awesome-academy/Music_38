package com.framgia.vhlee.musicplus.ui.search;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.framgia.vhlee.musicplus.R;
import com.framgia.vhlee.musicplus.data.model.Track;
import com.framgia.vhlee.musicplus.service.MediaRequest;
import com.framgia.vhlee.musicplus.service.MyService;
import com.framgia.vhlee.musicplus.ui.LoadMoreAbstract;
import com.framgia.vhlee.musicplus.ui.MiniPlayerClass;
import com.framgia.vhlee.musicplus.ui.adapter.TrackAdapter;
import com.framgia.vhlee.musicplus.ui.dialog.FeatureTrackDialog;
import com.framgia.vhlee.musicplus.util.StringUtil;

import java.util.List;

public class SearchActivity extends LoadMoreAbstract
        implements SearchContract.View,
        View.OnClickListener, TrackAdapter.OnClickItemSongListener {
    private EditText mEditInput;
    private ImageView mImageBack;
    private ImageView mImageClear;
    private ProgressBar mProgressBar;
    private TrackAdapter mTrackAdapter;
    private SearchPresenter mPresenter;
    private MiniPlayerClass mMiniPlayerClass;
    private List<Track> mTracks;
    private MyService mService;
    private int mOffset;
    private String mKeyword;
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
                    Toast.makeText(SearchActivity.this, (String) msg.obj,
                            Toast.LENGTH_SHORT).show();
                    break;
                case MediaRequest.PAUSED:
                    mMiniPlayerClass.pause();
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
        setContentView(R.layout.activity_search);
        initViewLoadMore();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyService.setUIHandler(mHandler);
        updateMiniPlayer();
    }

    @Override
    protected void onDestroy() {
        if (mService != null) unbindService(mConnection);
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_clear:
                mEditInput.setText("");
                break;
            case R.id.image_back:
                super.onBackPressed();
                break;
            default:
                break;
        }
    }

    @Override
    public void loadMoreData() {
        mIsScrolling = false;
        mProgressBar.setVisibility(View.VISIBLE);
        mPresenter.searchTracks(StringUtil.initSearchApi(mKeyword, ++mOffset));
    }

    @Override
    public void initViewLoadMore() {
        mEditInput = findViewById(R.id.edit_search);
        mImageBack = findViewById(R.id.image_back);
        mImageClear = findViewById(R.id.image_clear);
        mProgressBar = findViewById(R.id.progress_search_more);
        mRecyclerView = findViewById(R.id.recycler_search);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mTrackAdapter = new TrackAdapter(this);
        mPresenter = new SearchPresenter(this);
        mMiniPlayerClass = new MiniPlayerClass(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mTrackAdapter);
        mProgressBar.setVisibility(View.GONE);
        setListener();
        setLoadMore();
    }

    @Override
    public void showResult(List<Track> tracks) {
        Intent serviceIntent = MyService.getMyServiceIntent(SearchActivity.this);
        if (mService == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            } else startService(serviceIntent);
        }
        bindService(serviceIntent, mConnection, BIND_AUTO_CREATE);
        if (mOffset == 0) {
            mTrackAdapter.updateTracks(tracks);
            mTracks = tracks;
        } else {
            mTrackAdapter.addTracks(tracks);
            mTracks.addAll(tracks);
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void showNoResult(String message) {

    }

    @Override
    public void clickItemSongListener(int position) {
        mService.setTracks(mTracks);
        mService.requestCreate(position);
    }

    @Override
    public void showDialodFeatureTrack(int position) {
        FeatureTrackDialog dialog = new FeatureTrackDialog(SearchActivity.this,
                R.style.ThemeDialog, mTracks.get(position));
        dialog.show();
    }

    private void setListener() {
        mImageBack.setOnClickListener(this);
        mImageClear.setOnClickListener(this);
        mEditInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence keyword, int i, int i1, int i2) {
                mOffset = 0;
                mKeyword = keyword.toString();
                if (mKeyword.isEmpty()) mImageClear.setImageResource(R.drawable.ic_arrow_right);
                else mImageClear.setImageResource(R.drawable.ic_delete);
                mPresenter.searchTracks(StringUtil.initSearchApi(mKeyword, 0));
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void updateMiniPlayer() {
        Message message = new Message();
        message.what = MediaRequest.UPDATE_MINI_PLAYER;
        mHandler.sendMessage(message);
    }
}
