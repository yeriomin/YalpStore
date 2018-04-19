package com.dragons.aurora.downloader;

import android.util.Pair;

import com.dragons.aurora.task.RepeatingTask;

public class DownloadManagerProgressUpdater extends RepeatingTask {

    private long downloadId;
    private DownloadManagerAdapter dm;

    public DownloadManagerProgressUpdater(long downloadId, DownloadManagerAdapter dm) {
        this.downloadId = downloadId;
        this.dm = dm;
    }

    @Override
    protected void payload() {
        final Pair<Integer, Integer> progress = dm.getProgress(downloadId);
        if (null == progress) {
            return;
        }
        DownloadState state = DownloadState.get(downloadId);
        if (null == state) {
            return;
        }
        state.setProgress(downloadId, progress.first, progress.second);
    }

    @Override
    protected boolean shouldRunAgain() {
        DownloadState state = DownloadState.get(downloadId);
        return null != state && !state.isEverythingFinished();
    }
}
