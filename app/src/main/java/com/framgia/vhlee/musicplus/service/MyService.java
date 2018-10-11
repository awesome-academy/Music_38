package com.framgia.vhlee.musicplus.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.View;
import android.widget.RemoteViews;

import com.framgia.vhlee.musicplus.R;
import com.framgia.vhlee.musicplus.data.model.Track;
import com.framgia.vhlee.musicplus.mediaplayer.MediaPlayerManager;
import com.framgia.vhlee.musicplus.mediaplayer.PlayMusicInterface;
import com.framgia.vhlee.musicplus.ui.MainActivity;
import com.framgia.vhlee.musicplus.util.Constants;

import java.util.List;

public class MyService extends Service implements MediaPlayerManager.OnLoadingTrackListener, PlayMusicInterface {
    private static final int WHAT_CREATE = 1;
    private static final int WHAT_CHANGE_SONG = 2;
    private static final int WHAT_REQUEST_START = 3;
    private static final int WHAT_REQUEST_PAUSE = 4;
    private static final String WORKER_THREAD_NAME = "ServiceStartArguments";
    private static final int NOTIFI_ID = 9596;
    private static final int VALUE_NEXT_SONG = 4;
    private static final int VALUE_PREVIOUS_SONG = 5;
    private static final int VALUE_PLAY_SONG = 6;
    public static final String EXTRA_REQUEST_CODE = "REQUEST_CODE";
    private final IBinder mBinder = new LocalBinder();
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private static Handler mUIHandler;
    private MediaPlayerManager mMediaPlayerManager;
    private RemoteViews mNotificationLayout;
    private NotificationCompat.Builder mBuilder;
    private PendingIntent mNextPendingIntent;
    private PendingIntent mPreviousPendingIntent;
    private PendingIntent mPlayPendingIntent;

    @Override
    public void onCreate() {
        super.onCreate();
        mMediaPlayerManager = MediaPlayerManager.getsInstance(this, this);
        HandlerThread thread = new HandlerThread(WORKER_THREAD_NAME);
        thread.start();
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            int request = intent.getIntExtra(EXTRA_REQUEST_CODE, 0);
            switch (request) {
                case VALUE_NEXT_SONG:
                    requestChangeSong(Constants.NEXT_SONG);
                    break;
                case VALUE_PREVIOUS_SONG:
                    requestChangeSong(Constants.PREVIOUS_SONG);
                    break;
                case VALUE_PLAY_SONG:
                    if (isPlaying()) {
                        requestPause();
                    } else {
                        requestStart();
                    }
                    break;
            }
        }
        return START_STICKY;
    }

    @Override
    public void onStartLoading(int index) {
        if (mBuilder == null) {
            createMusicNotification();
        } else {
            updateNotification(index);
        }
        if (mUIHandler != null) {
            Message message = new Message();
            message.arg1 = index;
            message.what = MediaRequest.LOADING;
            mUIHandler.sendMessage(message);
        }
    }

    @Override
    public void onLoadingFail(String message) {
        Message msg = new Message();
        msg.what = MediaRequest.FAILURE;
        msg.obj = message;
        mUIHandler.sendMessage(msg);
    }

    @Override
    public void onLoadingSuccess() {
        updateNotification();
        mUIHandler.sendEmptyMessage(MediaRequest.SUCCESS);
    }

    @Override
    public void onTrackPaused() {
        updateNotification();
        mUIHandler.sendEmptyMessage(MediaRequest.PAUSED);
    }

    @Override
    public void create(int index) {
        mMediaPlayerManager.create(index);
    }

    @Override
    public void prepareAsync() {
        mMediaPlayerManager.prepareAsync();
    }

    @Override
    public void start() {
        mMediaPlayerManager.start();
    }

    @Override
    public void pause() {
        mMediaPlayerManager.pause();
    }

    @Override
    public int getDuration() {
        return mMediaPlayerManager.getDuration();
    }

    @Override
    public int getCurrrentPosition() {
        return mMediaPlayerManager.getCurrrentPosition();
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayerManager.isPlaying();
    }

    @Override
    public void seek(int possition) {
        mMediaPlayerManager.seek(possition);
    }

    @Override
    public void loop(boolean isLoop) {
        mMediaPlayerManager.loop(isLoop);
    }

    @Override
    public int getSong() {
        return mMediaPlayerManager.getSong();
    }

    @Override
    public void changeSong(int i) {
        mMediaPlayerManager.changeSong(i);
    }

    public void requestCreate(int index) {
        Track track = getTracks().get(index);
        initLayoutNotification(R.layout.layout_notification, track.getTitle());
        Message message = new Message();
        message.what = WHAT_CREATE;
        message.arg1 = index;
        mServiceHandler.sendMessage(message);
        createNextPendingIntent();
        createPreviousPendingIntent();
        createPlayPendingIntent();
    }

    public void requestChangeSong(int index) {
        Message message = new Message();
        message.what = WHAT_CHANGE_SONG;
        message.arg1 = index;
        mServiceHandler.sendMessage(message);
    }

    public void requestStart() {
        mServiceHandler.sendEmptyMessage(WHAT_REQUEST_START);
    }

    public void requestPause() {
        mServiceHandler.sendEmptyMessage(WHAT_REQUEST_PAUSE);
    }

    public static void setUIHandler(Handler uiHandler) {
        mUIHandler = uiHandler;
    }

    public MediaPlayer getMediaPlayer() {
        return mMediaPlayerManager.getMediaPlayer();
    }

    public List<Track> getTracks() {
        return mMediaPlayerManager.getTracks();
    }

    public MyService setTracks(List<Track> tracks) {
        mMediaPlayerManager.setTracks(tracks);
        return this;
    }

    public MediaPlayerManager getMediaPlayerManager() {
        return mMediaPlayerManager;
    }

    private void initLayoutNotification(int resourceLayout, String songName) {
        mNotificationLayout = new RemoteViews(getPackageName(), resourceLayout);
        mNotificationLayout.setTextViewText(R.id.text_song_name, songName);
        mNotificationLayout.setImageViewResource(R.id.image_play, R.drawable.ic_pause_white);
        mNotificationLayout.setViewVisibility(R.id.image_play, View.INVISIBLE);
    }

    private void createMusicNotification() {
        mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_album_white)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setContent(mNotificationLayout);
        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPenddingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPenddingIntent);
        startForeground(NOTIFI_ID, mBuilder.build());
    }

    private void updateNotification(int index) {
        Track track = getTracks().get(index);
        mNotificationLayout.setTextViewText(R.id.text_song_name, track.getTitle());
        if (isPlaying()) {
            mNotificationLayout.setImageViewResource(R.id.image_play, R.drawable.ic_pause_white);
            mBuilder.setOngoing(false);
        } else {
            mNotificationLayout.setImageViewResource(R.id.image_play, R.drawable.ic_play_button_white);
            mBuilder.setOngoing(true);
        }
        mNotificationLayout.setViewVisibility(R.id.image_play, View.INVISIBLE);
        mBuilder.setContent(mNotificationLayout);
        startForeground(NOTIFI_ID, mBuilder.build());
    }

    private void updateNotification() {
        mNotificationLayout.setViewVisibility(R.id.image_play, View.VISIBLE);
        if (isPlaying()) {
            mNotificationLayout.setImageViewResource(R.id.image_play, R.drawable.ic_play_button_white);
            mBuilder.setOngoing(false);
            mBuilder.setContent(mNotificationLayout);
            startForeground(NOTIFI_ID, mBuilder.build());
            stopForeground(false);
        } else {
            mNotificationLayout.setImageViewResource(R.id.image_play, R.drawable.ic_pause_white);
            mBuilder.setContent(mNotificationLayout);
            startForeground(NOTIFI_ID, mBuilder.build());
        }
    }

    private void createNextPendingIntent() {
        Intent nextIntent = new Intent(getApplicationContext(), MyService.class);
        nextIntent.putExtra(EXTRA_REQUEST_CODE, VALUE_NEXT_SONG);
        mNextPendingIntent = PendingIntent.getService(getApplicationContext(),
                VALUE_NEXT_SONG, nextIntent, 0);
        mNotificationLayout.setOnClickPendingIntent(R.id.image_next, mNextPendingIntent);
    }

    private void createPreviousPendingIntent() {
        Intent nextIntent = new Intent(getApplicationContext(), MyService.class);
        nextIntent.putExtra(EXTRA_REQUEST_CODE, VALUE_PREVIOUS_SONG);
        mPreviousPendingIntent = PendingIntent.getService(getApplicationContext(),
                VALUE_PREVIOUS_SONG, nextIntent, 0);
        mNotificationLayout.setOnClickPendingIntent(R.id.image_previous, mPreviousPendingIntent);
    }

    private void createPlayPendingIntent() {
        Intent nextIntent = new Intent(getApplicationContext(), MyService.class);
        nextIntent.putExtra(EXTRA_REQUEST_CODE, VALUE_PLAY_SONG);
        mPlayPendingIntent = PendingIntent.getService(getApplicationContext(),
                VALUE_PLAY_SONG, nextIntent, 0);
        mNotificationLayout.setOnClickPendingIntent(R.id.image_play, mPlayPendingIntent);
    }

    public class LocalBinder extends Binder {
        public MyService getService() {
            return MyService.this;
        }
    }

    public static Intent getMyServiceIntent(Context context) {
        Intent intent = new Intent(context, MyService.class);
        return intent;
    }

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_CREATE:
                    create(msg.arg1);
                    break;
                case WHAT_CHANGE_SONG:
                    changeSong(msg.arg1);
                    break;
                case WHAT_REQUEST_START:
                    start();
                    break;
                case WHAT_REQUEST_PAUSE:
                    pause();
                    break;
            }
        }
    }
}
