package com.framgia.vhlee.musicplus.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.widget.Toast;

import com.framgia.vhlee.musicplus.R;
import com.framgia.vhlee.musicplus.data.model.Track;
import com.framgia.vhlee.musicplus.util.Constants;
import com.framgia.vhlee.musicplus.util.StringUtil;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadService extends IntentService implements DownloadListener {
    private static final String TAG = "DownloadService";
    private static final String ROOT_FOLDER = "storage/emulated/0/download";
    private static final String MP3_FORMAT = ".mp3";
    public static final String LOACATION_HEADER = "Location";
    private static final String PERCENT = "%";
    private static final char DOT = '.';
    private static final int INVALID = -1;
    public static final int PROGRESS_MAX = 100;
    private static final int PERCENT_UNIT = 100;
    private static final int NOTIFICATION_ID = 111;
    private static final int SIZE_UNIT = 1024;
    private NotificationManager mNotificationManager;
    private Notification.Builder mBuilder;
    private ResultReceiver mResultReceiver;

    public DownloadService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initNotification();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Track track = (Track) intent.getSerializableExtra(Constants.EXTRA_TRACK);
        String urlDownload = track.getDownloadUrl();
        mResultReceiver = new DownloadReceiver(this, new Handler(Looper.getMainLooper()));
        try {
            URL url = new URL(urlDownload);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            boolean redirect = false;
            int status = connection.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                if (status == HttpURLConnection.HTTP_MOVED_TEMP
                        || status == HttpURLConnection.HTTP_MOVED_PERM
                        || status == HttpURLConnection.HTTP_SEE_OTHER)
                    redirect = true;
            }
            if (redirect) {
                String newUrl = connection.getHeaderField(LOACATION_HEADER);
                connection = (HttpURLConnection) new URL(newUrl).openConnection();
            }
            InputStream input = new BufferedInputStream(connection.getInputStream());
            downloadFile(input, track.getTitle(), connection.getContentLength());
            input.close();
            connection.disconnect();
        } catch (IOException e) {
            sendRequest(Constants.EXTRA_ERROR,
                    e.getMessage(), DownloadRequest.ERROR);
        }
    }

    @Override
    public void onPrepare(String title) {
        Toast.makeText(this,
                StringUtil.append(getString(R.string.notify_downloading), title),
                Toast.LENGTH_LONG).show();
        createNotification(title);
    }

    @Override
    public void onDownloading(int progress) {
        updateNotification(progress);
    }

    @Override
    public void onSuccess() {
        mBuilder.setProgress(0, 0, false)
                .setContentText(getString(R.string.notify_download_complete))
                .setOngoing(false)
                .setVibrate(new long[]{Notification.DEFAULT_VIBRATE})
                .setPriority(Notification.PRIORITY_MAX);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    @Override
    public void onFailure(String message) {
        // TODO Handle event download fail
    }

    public void downloadFile(InputStream input, String title, int fileLength) {
        try {
            String fileName = StringUtil.append(ROOT_FOLDER, title, MP3_FORMAT);
            OutputStream output = new FileOutputStream(fileName);
            byte data[] = new byte[SIZE_UNIT];
            long total = 0;
            int count;
            sendRequest(Constants.EXTRA_TITLE, title, DownloadRequest.PREPARE);
            while ((count = input.read(data)) != INVALID) {
                total += count;
                int progress = (int) (total * PERCENT_UNIT / fileLength);
                Bundle result = new Bundle();
                result.putInt(Constants.EXTRA_PROGRESS, progress);
                mResultReceiver.send(DownloadRequest.UPDATE, result);
                output.write(data, 0, count);
            }
            sendRequest(null, null, DownloadRequest.FINISH);
            output.flush();
            output.close();
        } catch (IOException e) {
            sendRequest(Constants.EXTRA_ERROR, e.getMessage(), DownloadRequest.ERROR);
        }
    }

    private void initNotification() {
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mBuilder = new Notification.Builder(this);
        mBuilder.setSmallIcon(R.drawable.ic_launcher_background)
                .setContentText(getString(R.string.notify_downloading))
                .setOngoing(true);
    }

    private void createNotification(String title) {
        mBuilder.setContentTitle(title);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    public void updateNotification(int progress) {
        String contentText = StringUtil.append(getString(R.string.notify_downloading),
                String.valueOf(progress), PERCENT);
        mBuilder.setContentText(contentText)
                .setProgress(PROGRESS_MAX, progress, false);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private void sendRequest(String key, String value, @DownloadRequest int requestCode) {
        Bundle bundle = new Bundle();
        bundle.putString(key, value);
        mResultReceiver.send(requestCode, bundle);
    }

    public static Intent getDownloadIntent(Context context, Track track) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra(Constants.EXTRA_TRACK, track);
        return intent;
    }
}
