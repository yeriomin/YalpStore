package com.github.yeriomin.yalpstore;

import android.os.Handler;
import android.os.Looper;
import android.util.Pair;

public class DownloadManagerProgressUpdater {

    static private final int INTERVAL = 100;

    private long downloadId;
    private DownloadManagerAdapter dm;
    private OnDownloadProgressListener listener;

    public DownloadManagerProgressUpdater(long downloadId, DownloadManagerAdapter dm, OnDownloadProgressListener listener) {
        this.downloadId = downloadId;
        this.dm = dm;
        this.listener = listener;
    }

    public void update() {
        final Pair<Integer, Integer> progress = dm.getProgress(downloadId);
        if (null == progress) {
            return;
        }
        new Handler(Looper.getMainLooper()).postDelayed(
            new Runnable() {
                @Override
                public void run() {
                    DownloadState.get(downloadId).setProgress(downloadId, progress.first, progress.second);
                    listener.onDownloadProgress();
                    if (DownloadState.get(downloadId).isEverythingFinished()) {
                        return;
                    }
                    update();
                }
            },
            INTERVAL
        );
    }
}
