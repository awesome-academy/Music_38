package com.framgia.vhlee.musicplus.ui.play;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.framgia.vhlee.musicplus.R;
import com.framgia.vhlee.musicplus.data.model.Track;
import com.framgia.vhlee.musicplus.service.DownloadService;
import com.framgia.vhlee.musicplus.service.MediaRequest;
import com.framgia.vhlee.musicplus.service.MyService;

public class PlayActivity extends AppCompatActivity
        implements View.OnClickListener, ServiceConnection {
    private static final String ARTWORK_DEFAULT_SIZE = "large";
    private static final String ARTWORK_MAX_SIZE = "t500x500";
    private static final int REQUEST_PERMISSION = 10;
    private ImageView mImageClose;
    private ImageView mImageArtwork;
    private ImageView mImageDownload;
    private TextView mTextTitle;
    private TextView mTextArtist;
    private ImageView mImagePrevious;
    private ImageView mImageNext;
    private ImageView mImagePlay;
    private boolean mHasPermission;
    private Track mTrack;
    private MyService mService;
    private ServiceConnection mConnection;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MediaRequest.LOADING:
                    updateUI(msg.arg1);
                    break;
                case MediaRequest.SUCCESS:
                    break;
                case MediaRequest.UPDATE_PLAY_ACTIVITY:
                    updateUI(msg.arg1);
                    break;
                case MediaRequest.UPDATE_MINI_PLAYER:
                    break;
                case MediaRequest.FAILURE:
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        initUI();
        bindMyService();
        checkPermission();
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        MyService.LocalBinder binder = (MyService.LocalBinder) iBinder;
        mService = binder.getService();
        mService.setUIHandler(mHandler);
        requestUpdateUI();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        unbindService(mConnection);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_close:
                super.onBackPressed();
                break;
            case R.id.image_download:
                if (mHasPermission) beginDownload();
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0] != PackageManager.PERMISSION_DENIED) {
                    mHasPermission = true;
                } else checkPermission();
                break;
            default:
                break;
        }
    }

    private void initUI() {
        mHasPermission = false;
        mImageClose = findViewById(R.id.image_close);
        mImageDownload = findViewById(R.id.image_download);
        mTextTitle = findViewById(R.id.text_title);
        mTextArtist = findViewById(R.id.text_artist);
        mImageArtwork = findViewById(R.id.image_artwork);
        mImagePrevious = findViewById(R.id.image_previous);
        mImageNext = findViewById(R.id.image_next);
        mImagePlay = findViewById(R.id.image_play);
        setListener();
    }

    private void setListener() {
        mImageClose.setOnClickListener(this);
        mImageDownload.setOnClickListener(this);
        mImagePrevious.setOnClickListener(this);
        mImageNext.setOnClickListener(this);
        mImagePlay.setOnClickListener(this);
    }

    private void bindMyService() {
        mConnection = this;
        if (mService == null) startService(MyService.getMyServiceIntent(PlayActivity.this));
        bindService(MyService.getMyServiceIntent(PlayActivity.this), mConnection, BIND_AUTO_CREATE);
    }

    private void updateUI(int index) {
        mTrack = mService.getTracks().get(index);
        if (mTrack.isDownloadable()) {
            mImageDownload.setImageResource(R.drawable.ic_download);
            mImageDownload.setClickable(true);
        } else {
            mImageDownload.setImageResource(R.drawable.ic_download_false);
            mImageDownload.setClickable(false);
        }
        mTextTitle.setText(mTrack.getTitle());
        mTextArtist.setText(mTrack.getArtist());
        String artworkCover = mTrack.getArtworkUrl()
                .replace(ARTWORK_DEFAULT_SIZE, ARTWORK_MAX_SIZE);
        setImage(mImageArtwork, artworkCover);
    }

    private void beginDownload() {
        Intent intent = DownloadService.getDownloadIntent(this, mTrack);
        startService(intent);
    }

    private void requestUpdateUI() {
        Message message = new Message();
        message.what = MediaRequest.UPDATE_PLAY_ACTIVITY;
        message.arg1 = mService.getSong();
        mHandler.sendMessage(message);
    }

    private void checkPermission() {
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(this, permissions[0])
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(permissions, REQUEST_PERMISSION);
        } else mHasPermission = true;
    }

    public void setImage(ImageView image, String source) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(R.drawable.default_artwork);
        Glide.with(image.getContext())
                .load(source)
                .apply(requestOptions)
                .into(image);
    }
}
