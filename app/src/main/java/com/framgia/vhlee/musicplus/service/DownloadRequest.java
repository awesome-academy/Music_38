package com.framgia.vhlee.musicplus.service;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({
        DownloadRequest.PREPARE, DownloadRequest.UPDATE,
        DownloadRequest.FINISH, DownloadRequest.ERROR
})
public @interface DownloadRequest {
    int PREPARE = 101;
    int UPDATE = 102;
    int FINISH = 103;
    int ERROR = 104;
}
