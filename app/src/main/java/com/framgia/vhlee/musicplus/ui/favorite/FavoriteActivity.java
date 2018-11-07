package com.framgia.vhlee.musicplus.ui.favorite;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.framgia.vhlee.musicplus.R;
import com.framgia.vhlee.musicplus.data.model.Track;
import com.framgia.vhlee.musicplus.data.repository.TrackRepository;
import com.framgia.vhlee.musicplus.data.source.local.TrackLocalDataSource;
import com.framgia.vhlee.musicplus.data.source.remote.TrackRemoteDataSource;
import com.framgia.vhlee.musicplus.service.MediaRequest;
import com.framgia.vhlee.musicplus.service.MyService;
import com.framgia.vhlee.musicplus.ui.MiniPlayerClass;
import com.framgia.vhlee.musicplus.ui.adapter.TrackAdapter;

import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity
        implements FavoriteContract.View, TrackAdapter.OnClickItemSongListener,
        TrackAdapter.OnClickDeleteListener {

    private RecyclerView mRecyclerView;
    private TrackAdapter mAdapter;
    private List<Track> mTracks;
    private FavoriteContract.Presenter mPresenter;
    private MyService mService;
    private MiniPlayerClass mMiniPlayerClass;
    private int mDeletePosition;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MyService.LocalBinder binder = (MyService.LocalBinder) iBinder;
            mService = binder.getService();
            MyService.setUIHandler(mHandler);
            mMiniPlayerClass.setService(mService);
            updateMiniPlayer();
            initData();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            unbindService(mConnection);
        }
    };

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
                    Toast.makeText(FavoriteActivity.this, (String) msg.obj,
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        initPresenter();
        initToolbar();
        initView();
        Intent serviceIntent = MyService.getMyServiceIntent(FavoriteActivity.this);
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
    protected void onResume() {
        super.onResume();
        MyService.setUIHandler(mHandler);
        updateMiniPlayer();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }

    private void initToolbar() {
        getSupportActionBar().setTitle(R.string.string_favorites);
    }

    private void initData() {
        mPresenter.getFavotiteTracks();
    }

    private void initView() {
        mMiniPlayerClass = new MiniPlayerClass(this);
        mTracks = new ArrayList<>();
        mRecyclerView = findViewById(R.id.recycler_favorite);
        mAdapter = new TrackAdapter(mTracks, this, this);
        mAdapter.setFavarite(true);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void updateMiniPlayer() {
        Message message = new Message();
        message.what = MediaRequest.UPDATE_MINI_PLAYER;
        mHandler.sendMessage(message);
    }

    @Override
    public void onGetTracksSuccess(List<Track> tracks) {
        if (tracks != null && tracks.size() > 0) {
            mTracks.addAll(tracks);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onFail(String message) {

    }

    @Override
    public void onDeleteTracksSuccess(boolean isSuccess) {
        if (isSuccess) {
            Toast.makeText(this, R.string.text_delete_success, Toast.LENGTH_SHORT).show();
            mTracks.remove(mDeletePosition);
            mAdapter.notifyItemRemoved(mDeletePosition);
        }
    }

    @Override
    public void clickItemSongListener(int position) {
        mService.setTracks(mTracks);
        mService.requestCreate(position);
    }

    @Override
    public void showDialodFeatureTrack(int position) {

    }

    @Override
    public void deleteFromFavorite(int position) {
        mDeletePosition = position;
        mPresenter.deleteFavoriteTrack(mTracks.get(position));
    }

    private void initPresenter() {
        TrackRepository repository = TrackRepository.getInstance(
                TrackRemoteDataSource.getsInstance(),
                TrackLocalDataSource.getInstance(getApplicationContext())
        );
        mPresenter = new FavoritePresenter(repository, this);
    }
}
