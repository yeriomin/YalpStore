package com.github.yeriomin.yalpstore;

import android.view.View;
import android.widget.ProgressBar;

import java.lang.ref.WeakReference;

public class DownloadProgressBarUpdater extends DownloadProgressUpdater {

    private WeakReference<ProgressBar> progressBarRef = new WeakReference<>(null);

    public DownloadProgressBarUpdater(String packageName, ProgressBar progressBar) {
        super(packageName);
        progressBarRef = new WeakReference<>(progressBar);
    }

    @Override
    protected void setProgress(int progress, int max) {
        ProgressBar progressBar = progressBarRef.get();
        if (null == progressBar) {
            return;
        }
        progressBar.setIndeterminate(false);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(progress);
        progressBar.setMax(max);
    }

    @Override
    protected void finish() {
        ProgressBar progressBar = progressBarRef.get();
        if (null == progressBar) {
            return;
        }
        progressBar.setVisibility(View.INVISIBLE);
        progressBar.setIndeterminate(true);
    }
}
