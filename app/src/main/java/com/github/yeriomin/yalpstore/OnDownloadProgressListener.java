package com.github.yeriomin.yalpstore;

import android.util.Pair;
import android.widget.ProgressBar;

import java.lang.ref.WeakReference;

public class OnDownloadProgressListener {

    static private final long UPDATE_INTERVAL = 300;

    static private long lastUpdate = 0;

    private WeakReference<ProgressBar> progressBarRef = new WeakReference<>(null);
    private DownloadState state;

    public OnDownloadProgressListener(ProgressBar progressBar, DownloadState state) {
        progressBarRef = new WeakReference<>(progressBar);
        this.state = state;
    }

    public synchronized void onDownloadProgress() {
        if (lastUpdate + UPDATE_INTERVAL > System.currentTimeMillis()) {
            return;
        }
        lastUpdate = System.currentTimeMillis();
        ProgressBar progressBar = progressBarRef.get();
        if (null == progressBar) {
            return;
        }
        Pair<Integer, Integer> progress = state.getProgress();
        if (!state.isEverythingFinished()) {
            progressBar.setProgress(progress.first);
            progressBar.setMax(progress.second);
        }
    }
}
