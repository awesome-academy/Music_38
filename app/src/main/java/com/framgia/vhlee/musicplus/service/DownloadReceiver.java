package com.framgia.vhlee.musicplus.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import com.framgia.vhlee.musicplus.util.Constants;

public class DownloadReceiver extends ResultReceiver {
    private DownloadListener mDownloadListener;

    public DownloadReceiver(DownloadListener listener, Handler handler) {
        super(handler);
        mDownloadListener = listener;
    }

    @Override
    protected void onReceiveResult(@DownloadRequest int resultCode, Bundle resultData) {
        super.onReceiveResult(resultCode, resultData);
        switch (resultCode) {
            case DownloadRequest.PREPARE:
                String title = resultData.getString(Constants.EXTRA_TITLE);
                mDownloadListener.onPrepare(title);
                break;
            case DownloadRequest.UPDATE:
                int progress = resultData.getInt(Constants.EXTRA_PROGRESS);
                mDownloadListener.onDownloading(progress);
                break;
            case DownloadRequest.FINISH:
                mDownloadListener.onSuccess();
                break;
            case DownloadRequest.ERROR:
                String message = resultData.getString(Constants.EXTRA_ERROR);
                mDownloadListener.onFailure(message);
                break;
            default:
                break;
        }
    }
}
