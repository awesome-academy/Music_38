package com.framgia.vhlee.musicplus.service;

public interface DownloadListener {
    void onPrepare(String title);

    void onDownloading(int progress);

    void onSuccess();

    void onFailure(String message);
}
