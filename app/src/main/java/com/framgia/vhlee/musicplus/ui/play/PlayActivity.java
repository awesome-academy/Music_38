package com.framgia.vhlee.musicplus.ui.play;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.framgia.vhlee.musicplus.R;
import com.framgia.vhlee.musicplus.data.model.Track;
import com.framgia.vhlee.musicplus.mediaplayer.MediaPlayerSetting;
import com.framgia.vhlee.musicplus.mediaplayer.PlayMusicInterface;
import com.framgia.vhlee.musicplus.service.DownloadService;
import com.framgia.vhlee.musicplus.service.MediaRequest;
import com.framgia.vhlee.musicplus.service.MyService;
import com.framgia.vhlee.musicplus.ui.SimpleItemTouchHelperCallback;
import com.framgia.vhlee.musicplus.ui.adapter.TrackAdapter;
import com.framgia.vhlee.musicplus.ui.dialog.FeatureTrackDialog;
import com.framgia.vhlee.musicplus.util.Constants;
import com.framgia.vhlee.musicplus.util.StringUtil;
import com.framgia.vhlee.musicplus.util.TimeUtil;

import java.io.File;
import java.util.List;

public class PlayActivity extends AppCompatActivity implements View.OnClickListener,
        SeekBar.OnSeekBarChangeListener, ServiceConnection, TrackAdapter.OnClickItemSongListener,
        SimpleItemTouchHelperCallback.ItemTouchListenner, TrackAdapter.OnDragDropListener,
        DialogInterface.OnClickListener, PlayContract.View {
    private static final long MESSAGE_UPDATE_DELAY = 1000;
    private static final int REQUEST_PERMISSION = 10;
    private static final int WHAT_UPDATE_FOLLOWING_SERVICE = 1234;
    private static final String ROOT_FOLDER = "storage/emulated/0/download/";
    private static final String MP3_FORMAT = ".mp3";
    private static final String ARTWORK_DEFAULT_SIZE = "large";
    private static final String ARTWORK_MAX_SIZE = "t500x500";
    private boolean mHasPermission;
    private MyService mService;
    private TextView mCurrentPositionText;
    private ImageView mImageNow;
    private SeekBar mSeekBar;
    private TextView mDurationText;
    private ImageView mShuffleImage;
    private ImageView mLoopImage;
    private ImageView mImageClose;
    private DrawerLayout mDrawerLayout;
    private ImageView mImageDownload;
    private TextView mTextTitle;
    private TextView mTextArtist;
    private ImageView mImageArtwork;
    private ImageView mImagePrevious;
    private ImageView mImageNext;
    private ImageView mImagePlay;
    private ImageView mImageFavourite;
    private boolean mIsBoundService;
    private Track mTrack;
    private TrackAdapter mTrackAdapter;
    private ServiceConnection mConnection;
    private PlayContract.Presenter mPresenter;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MediaRequest.LOADING:
                    if (mIsBoundService) updateUI(msg.arg1);
                    startLoading(msg.arg1);
                    break;
                case MediaRequest.SUCCESS:
                    loadingSuccess();
                    break;
                case MediaRequest.UPDATE_PLAY_ACTIVITY:
                    updateUI(msg.arg1);
                    break;
                case MediaRequest.UPDATE_MINI_PLAYER:
                    break;
                case MediaRequest.PAUSED:
                    mImagePlay.setImageResource(R.drawable.ic_play);
                    break;
                case MediaRequest.FAILURE:
                    Toast.makeText(PlayActivity.this, (String) msg.obj,
                            Toast.LENGTH_SHORT).show();
                    break;
                case MediaRequest.STOPPED:
                    mImagePlay.setImageResource(R.drawable.ic_play);
                    break;
                case WHAT_UPDATE_FOLLOWING_SERVICE:
                    updateSeekBar();
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        mPresenter = new PlayPresenter(this, this);
        bindMyService();
        initUI();
    }

    protected void onStop() {
        super.onStop();
        if (mIsBoundService) {
            mIsBoundService = false;
        }
    }

    protected void onDestroy() {
        unbindService(mConnection);
        super.onDestroy();
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        MyService.LocalBinder binder = (MyService.LocalBinder) iBinder;
        mService = binder.getService();
        mService.setUIHandler(mHandler);
        requestUpdateUI();
        mIsBoundService = true;
        initLoopImage();
        initShuffleImage();
        initNavigation();
    }

    private void initNavigation() {
        RecyclerView recyclerNow = findViewById(R.id.recycler_now_playing);
        mTrackAdapter = new TrackAdapter(mService.getTracks(), this, this);
        mTrackAdapter.setNowPlaying(true);
        recyclerNow.setAdapter(mTrackAdapter);
        initItemTouchHelper(recyclerNow);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        mIsBoundService = false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_close:
                super.onBackPressed();
                break;
            case R.id.image_now_playing:
                mDrawerLayout.openDrawer(Gravity.END);
                break;
            case R.id.image_download:
                checkPermission();
                if (mHasPermission && isAcceptDownload(mTrack.getTitle())) beginDownload();
                break;
            case R.id.image_loop:
                changeLoopType();
                break;
            case R.id.image_previous:
                mService.requestChangeSong(Constants.PREVIOUS_SONG);
                break;
            case R.id.image_next:
                mService.requestChangeSong(Constants.NEXT_SONG);
                break;
            case R.id.image_play:
                playSong();
                break;
            case R.id.image_shuffle:
                changeShuffleType();
                break;
            case R.id.image_favourite:
                addToFavorites();
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int button) {
        switch (button) {
            case DialogInterface.BUTTON_POSITIVE:
                beginDownload();
                dialogInterface.dismiss();
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                dialogInterface.dismiss();
                break;
            default:
                break;
        }
    }

    @Override
    public void clickItemSongListener(int position) {
        mService.requestCreate(position);
        mDrawerLayout.closeDrawer(Gravity.END);
    }

    @Override
    public void showDialodFeatureTrack(int position) {
        FeatureTrackDialog dialog = new FeatureTrackDialog(PlayActivity.this,
                R.style.ThemeDialog, mService.getTracks().get(position));
        mDrawerLayout.closeDrawer(Gravity.END);
        dialog.show();
    }

    public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
        if (fromUser) {
            mService.requestSeek(i);
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
        mIsBoundService = true;
        mDrawerLayout = findViewById(R.id.drawer_play);
        mImageClose = findViewById(R.id.image_close);
        mImageNow = findViewById(R.id.image_now_playing);
        mImageDownload = findViewById(R.id.image_download);
        mTextTitle = findViewById(R.id.text_title);
        mTextArtist = findViewById(R.id.text_artist);
        mImageArtwork = findViewById(R.id.image_artwork);
        mImagePrevious = findViewById(R.id.image_previous);
        mImageNext = findViewById(R.id.image_next);
        mImagePlay = findViewById(R.id.image_play);
        mCurrentPositionText = findViewById(R.id.text_current_position);
        mSeekBar = findViewById(R.id.seekbar_track);
        mDurationText = findViewById(R.id.text_duration);
        mShuffleImage = findViewById(R.id.image_shuffle);
        mLoopImage = findViewById(R.id.image_loop);
        mImageFavourite = findViewById(R.id.image_favourite);
        setListener();
    }

    private void setListener() {
        mImageClose.setOnClickListener(this);
        mImageDownload.setOnClickListener(this);
        mImagePrevious.setOnClickListener(this);
        mImageNext.setOnClickListener(this);
        mImagePlay.setOnClickListener(this);
        mImageNow.setOnClickListener(this);
        mShuffleImage.setOnClickListener(this);
        mLoopImage.setOnClickListener(this);
        mSeekBar.setOnSeekBarChangeListener(this);
        mImageFavourite.setOnClickListener(this);
    }

    private void bindMyService() {
        mConnection = this;
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
        if (mTrack.getArtworkUrl() != null) {
            String artworkCover = mTrack.getArtworkUrl()
                    .replace(ARTWORK_DEFAULT_SIZE, ARTWORK_MAX_SIZE);
            setImage(mImageArtwork, artworkCover);
        } else mImageArtwork.setImageResource(R.drawable.default_artwork);
    }

    private void initItemTouchHelper(RecyclerView recyclerView) {
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void beginDownload() {
        Intent intent = DownloadService.getDownloadIntent(this, mTrack);
        startService(intent);
    }

    private void requestUpdateUI() {
        if (mService != null && mService.getMediaPlayer() != null) {
            updateUI();
        }
        Message message = new Message();
        message.what = MediaRequest.UPDATE_PLAY_ACTIVITY;
        message.arg1 = mService.getSong();
        mHandler.sendMessage(message);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onMove(int oldPosition, int newPosition) {
        mTrackAdapter.onMove(oldPosition, newPosition);
    }

    @Override
    public void swipe(int position, int direction) {
        mTrackAdapter.swipe(position, direction);
    }

    @Override
    public void onDropViewHolder(List<Track> tracks) {
        mService.setTracks(tracks);
    }

    @Override
    public void onSwipeViewHolder(List<Track> tracks) {
        mService.setTracks(tracks);
    }

    @Override
    public void onFail(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAddTracksSuccess(List<String> id) {
        Toast.makeText(this, R.string.text_add_success, Toast.LENGTH_SHORT).show();
    }

    private void initLoopImage() {
        int looptype = mService.getMediaPlayerManager().getLoopType();
        switch (looptype) {
            case MediaPlayerSetting.LoopType.NONE:
                mLoopImage.setImageResource(R.drawable.ic_loop_none);
                break;
            case MediaPlayerSetting.LoopType.ONE:
                mLoopImage.setImageResource(R.drawable.ic_loop_one);
                break;
            case MediaPlayerSetting.LoopType.ALL:
                mLoopImage.setImageResource(R.drawable.ic_loop_all);
                break;
            default:
                break;
        }
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

    public static Intent getPlayActivityIntent(Context context) {
        Intent intent = new Intent(context, PlayActivity.class);
        return intent;
    }

    private void updateUI() {
        int index = mService.getSong();
        Track track = mService.getTracks().get(index);
        int duration = mService.getDuration();
        Glide.with(this)
                .load(track.getArtworkUrl())
                .into(mImageArtwork);
        mTextTitle.setText(track.getTitle());
        mTextArtist.setText(track.getArtist());
        mSeekBar.setMax(mService.getDuration());
        mSeekBar.setProgress(mService.getCurrrentPosition());
        mDurationText.setText(TimeUtil.convertMilisecondToFormatTime(duration));
        updatePlayImage(mService.isPlaying());
        mHandler.sendEmptyMessageDelayed(WHAT_UPDATE_FOLLOWING_SERVICE,
                MESSAGE_UPDATE_DELAY);
    }

    private void updatePlayImage(boolean isPlaying) {
        if (isPlaying) {
            mImagePlay.setImageResource(R.drawable.ic_pause);
            return;
        }
        mImagePlay.setImageResource(R.drawable.ic_play);
    }

    public void loadingSuccess() {
        mSeekBar.setEnabled(true);
        int duration = mService.getDuration();
        mImagePlay.setVisibility(View.VISIBLE);
        mImagePlay.setImageResource(R.drawable.ic_pause);
        mSeekBar.setMax(mService.getDuration());
        mDurationText.setText(TimeUtil.convertMilisecondToFormatTime(duration));
    }

    public void startLoading(int index) {
        mSeekBar.setEnabled(false);
        mImagePlay.setVisibility(View.INVISIBLE);
        Track track = mService.getTracks().get(index);
        mTextArtist.setText(track.getArtist());
        mTextTitle.setText(track.getTitle());
        mSeekBar.setProgress(0);
        mCurrentPositionText.setText(TimeUtil.convertMilisecondToFormatTime(0));
        mDurationText.setText(TimeUtil.convertMilisecondToFormatTime(0));
        if (!this.isDestroyed()) {
            Glide.with(this)
                    .load(track.getArtworkUrl())
                    .into(mImageArtwork);
        }
    }

    public void pause() {
        mImagePlay.setImageResource(R.drawable.ic_play);
    }

    private void playSong() {
        if (mService.isPlaying()) {
            mService.requestPause();
            mImagePlay.setImageResource(R.drawable.ic_play);
            return;
        }
        mImagePlay.setImageResource(R.drawable.ic_pause);
        int mediaStatus = mService.getMediaPlayerManager().getStatus();
        if (mediaStatus == PlayMusicInterface.StatusPlayerType.STOPPED) {
            mService.requestPrepareAsync();
            return;
        }
        mService.requestStart();
    }

    private void changeLoopType() {
        int looptype = mService.getMediaPlayerManager().getLoopType();
        switch (looptype) {
            case MediaPlayerSetting.LoopType.NONE:
                mService.getMediaPlayerManager().setLoopType(MediaPlayerSetting.LoopType.ONE);
                mService.loop(true);
                initLoopImage();
                break;
            case MediaPlayerSetting.LoopType.ONE:
                mService.getMediaPlayerManager().setLoopType(MediaPlayerSetting.LoopType.ALL);
                mService.loop(false);
                initLoopImage();
                break;
            case MediaPlayerSetting.LoopType.ALL:
                mService.getMediaPlayerManager().setLoopType(MediaPlayerSetting.LoopType.NONE);
                mService.loop(false);
                initLoopImage();
                break;
            default:
                break;
        }
    }

    private void updateSeekBar() {
        int currentPosition = mService.getCurrrentPosition();
        mSeekBar.setProgress(currentPosition);
        mCurrentPositionText.setText(TimeUtil.convertMilisecondToFormatTime(currentPosition));
        mHandler.sendEmptyMessageDelayed(WHAT_UPDATE_FOLLOWING_SERVICE,
                MESSAGE_UPDATE_DELAY);
    }

    private void initShuffleImage() {
        int shuffleType = mService.getMediaPlayerManager().getShuffleType();
        switch (shuffleType) {
            case MediaPlayerSetting.ShuffleType.OFF:
                mShuffleImage.setImageResource(R.drawable.ic_shuffle_none);
                break;
            case MediaPlayerSetting.ShuffleType.ON:
                mShuffleImage.setImageResource(R.drawable.ic_shuffle);
                break;
            default:
                break;
        }
    }

    private void changeShuffleType() {
        int shuffleType = mService.getMediaPlayerManager().getShuffleType();
        switch (shuffleType) {
            case MediaPlayerSetting.ShuffleType.OFF:
                mService.getMediaPlayerManager().setShuffleType(MediaPlayerSetting.ShuffleType.ON);
                initShuffleImage();
                break;
            case MediaPlayerSetting.ShuffleType.ON:
                mService.getMediaPlayerManager().setShuffleType(MediaPlayerSetting.ShuffleType.OFF);
                initShuffleImage();
                break;
            default:
                break;
        }
    }

    private boolean isAcceptDownload(String title) {
        String fileName = StringUtil.append(ROOT_FOLDER, title, MP3_FORMAT);
        File file = new File(fileName);
        if (!file.isDirectory() && file.exists()) return confirmDownload();
        return true;
    }

    private boolean confirmDownload() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.notify_file_exists)
                .setMessage(R.string.notify_confirm_download)
                .setIcon(R.drawable.ic_download)
                .setPositiveButton(R.string.confirm_yes, this)
                .setNegativeButton(R.string.confirm_no, this)
                .create();
        dialog.show();
        return false;

    }

    private void addToFavorites() {
        mPresenter.addFavariteTrack(mTrack);
    }
}
