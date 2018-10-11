package com.framgia.vhlee.musicplus.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.framgia.vhlee.musicplus.R;
import com.framgia.vhlee.musicplus.service.MediaRequest;
import com.framgia.vhlee.musicplus.service.MyService;
import com.framgia.vhlee.musicplus.ui.home.HomeFragment;
import com.framgia.vhlee.musicplus.ui.mymusic.MyMusicFragment;

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private MiniPlayerClass mMiniPlayerClass;
    private MyService mService;

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
                    Toast.makeText(MainActivity.this, (String) msg.obj,
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
            mService.setUIHandler(mHandler);
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
        setContentView(R.layout.activity_main);
        initUI();
        Intent serviceIntent = MyService.getMyServiceIntent(MainActivity.this);
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
        selectTab(HomeFragment.newInstance());
    }

    @Override
    protected void onDestroy() {
        unbindService(mConnection);
        super.onDestroy();
    }

    /**
     * Navigation: item click event
     *
     * @param menuItem
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.navigation_home:
                selectTab(HomeFragment.newInstance());
                break;
            case R.id.navigation_my_music:
                selectTab(MyMusicFragment.newInstance());
                break;
            case R.id.navigation_settings:
                break;
            default:
                break;
        }
        return true;
    }

    private void initUI() {
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(this);
        mMiniPlayerClass = new MiniPlayerClass(this);
    }

    private void selectTab(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_fragments_container, fragment);
        transaction.commit();
    }

    private void updateMiniPlayer() {
        Message message = new Message();
        message.what = MediaRequest.UPDATE_MINI_PLAYER;
        mHandler.sendMessage(message);
    }

    @Override
    public void onClick(View view) {
        mMiniPlayerClass.onClick(view);
    }
}
