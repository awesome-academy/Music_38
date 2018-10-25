package com.framgia.vhlee.musicplus.ui.mymusic;


import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.framgia.vhlee.musicplus.R;
import com.framgia.vhlee.musicplus.data.model.Track;
import com.framgia.vhlee.musicplus.service.MediaRequest;
import com.framgia.vhlee.musicplus.service.MyService;
import com.framgia.vhlee.musicplus.ui.MiniPlayerClass;
import com.framgia.vhlee.musicplus.ui.adapter.TrackAdapter;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.BIND_AUTO_CREATE;

public class MyMusicFragment extends Fragment
        implements TrackAdapter.OnClickItemSongListener,
        MyMusicContract.View, View.OnClickListener {
    private static final String ROOT_FOLDER = "storage/emulated/0/";
    private static final int REQUEST_PERMISSION = 11;
    private boolean mHasPermission;
    private TrackAdapter mAdapter;
    private List<Track> mTracks;
    private MyMusicPresenter mPresenter;
    private MyService mService;
    private MiniPlayerClass mMiniPlayer;
    private MusicHandler mHandler;
    private MusicServiceConnection mConnection;

    public static MyMusicFragment newInstance() {
        MyMusicFragment fragment = new MyMusicFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_music, container, false);
        initUI(view);
        checkPermission();
        if (mHasPermission) initData();
        return view;
    }

    @Override
    public void onDestroy() {
        if (mService!= null) getActivity().unbindService(mConnection);
        super.onDestroy();
    }

    private void initUI(View view) {
        initRecycler(view);
        setListener();
    }

    private void initRecycler(View view) {
        RecyclerView recyclerOffline = view.findViewById(R.id.recycler_offline);
        mAdapter = new TrackAdapter(this);
        recyclerOffline.setAdapter(mAdapter);
    }

    private void setListener() {
        mMiniPlayer = new MiniPlayerClass(getActivity());
        mPresenter = new MyMusicPresenter(this);
        mHandler = new MusicHandler();
        mConnection = new MusicServiceConnection();
    }

    public void initData() {
        mPresenter.loadOffline();
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
    public void onSuccess(List<Track> tracks) {
        mAdapter.updateTracks(tracks);
        mTracks = tracks;
        Intent serviceIntent = MyService.getMyServiceIntent(getActivity());
        if (mService == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getActivity().startForegroundService(serviceIntent);
            } else {
                getActivity().startService(serviceIntent);
            }
        }
        getActivity().bindService(serviceIntent, mConnection, BIND_AUTO_CREATE);
    }

    @Override
    public void onFailure(String message) {
        // TODO Handle load local failure
    }

    private void updateMiniPlayer() {
        Message message = new Message();
        message.what = MediaRequest.UPDATE_MINI_PLAYER;
        mHandler.sendMessage(message);
    }

    @Override
    public void onClick(View view) {
        mMiniPlayer.onClick(view);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0] != PackageManager.PERMISSION_DENIED) {
                    mHasPermission = true;
                    initData();
                } else checkPermission();
                break;
            default:
                break;
        }
    }

    private void checkPermission() {
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(getActivity(), permissions[0])
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(permissions, REQUEST_PERMISSION);
        } else {
            mHasPermission = true;
        }
    }

    @Override
    public Context getContext() {
        return super.getContext();
    }

    class MusicHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MediaRequest.LOADING:
                    mMiniPlayer.startLoading(msg.arg1);
                    break;
                case MediaRequest.SUCCESS:
                    mMiniPlayer.loadingSuccess();
                    break;
                case MediaRequest.UPDATE_MINI_PLAYER:
                    if (mService != null && mService.getMediaPlayer() != null) {
                        mMiniPlayer.update();
                    }
                    break;
                case MediaRequest.FAILURE:
                    Toast.makeText(getActivity(), (String) msg.obj,
                            Toast.LENGTH_SHORT).show();
                    break;
                case MediaRequest.PAUSED:
                    mMiniPlayer.pause();
                    break;
                default:
                    break;
            }
        }
    }

    class MusicServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MyService.LocalBinder binder = (MyService.LocalBinder) iBinder;
            mService = binder.getService();
            mService.setUIHandler(mHandler);
            mMiniPlayer.setService(mService);
            updateMiniPlayer();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            getActivity().unbindService(mConnection);
        }
    }
}
