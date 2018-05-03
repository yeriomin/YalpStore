package com.dragons.aurora.downloader;

import android.util.Pair;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dragons.aurora.task.RepeatingTask;

import java.lang.ref.WeakReference;

public class DownloadProgressBarUpdater extends RepeatingTask {

    private String packageName;
    private WeakReference<ProgressBar> progressBar = new WeakReference<>(null);
    private WeakReference<TextView> progressCents = new WeakReference<>(null);

    public DownloadProgressBarUpdater(String packageName, ProgressBar progressBar, TextView progressCents) {
        this.packageName = packageName;
        this.progressBar = new WeakReference<>(progressBar);
        this.progressCents = new WeakReference<>(progressCents);
    }

    @Override
    protected boolean shouldRunAgain() {
        DownloadState state = DownloadState.get(packageName);
        return null != state && !state.isEverythingFinished();
    }

    @Override
    protected void payload() {
        ProgressBar progressBar = this.progressBar.get();
        TextView progressCents = this.progressCents.get();
        if (null == progressBar) {
            return;
        }
        DownloadState state = DownloadState.get(packageName);
        if (null == state || state.isEverythingFinished()) {
            progressBar.setVisibility(View.GONE);
            progressCents.setVisibility(View.GONE);
            progressBar.setIndeterminate(true);
            return;
        }
        Pair<Float, Float> progress = state.getProgress();

        progressBar.setVisibility(View.VISIBLE);
        progressCents.setVisibility(View.VISIBLE);

        progressBar.setIndeterminate(false);
        progressBar.setMax(100);

        progressBar.setProgress(getPercentage(progress.first, progress.second));
        progressCents.setText(String.valueOf(progressBar.getProgress()) + "%");
    }

    private int getPercentage(float cur, float total) {
        if (total != 0)
            return (int) ((cur * 100) / total);
        else
            return 0;
    }
}
