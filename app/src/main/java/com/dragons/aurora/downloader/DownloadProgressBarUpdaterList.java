package com.dragons.aurora.downloader;

import android.content.Context;
import android.util.Pair;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dragons.aurora.Util;
import com.dragons.aurora.task.RepeatingTask;
import com.dragons.aurora.view.UpdatableAppBadge;

public class DownloadProgressBarUpdaterList extends RepeatingTask {

    private Context context;
    private String packageName;
    private UpdatableAppBadge updatableAppBadge;

    public DownloadProgressBarUpdaterList(Context context, UpdatableAppBadge updatableAppBadge) {
        this.context = context;
        this.packageName = updatableAppBadge.getApp().getPackageName();
        this.updatableAppBadge = updatableAppBadge;
    }

    @Override
    protected boolean shouldRunAgain() {
        DownloadState state = DownloadState.get(packageName);
        return null != state && !state.isEverythingFinished();
    }

    @Override
    protected void payload() {
        ProgressBar progressBar = updatableAppBadge.progressBar;
        TextView progressCents = updatableAppBadge.progressCents;

        if (null == progressBar) {
            return;
        }

        DownloadState state = DownloadState.get(packageName);
        if (null == state || state.isEverythingFinished()) {
            progressBar.setVisibility(View.GONE);
            progressCents.setVisibility(View.GONE);
            progressBar.setIndeterminate(true);

            if (Util.isAlreadyDownloaded(context, updatableAppBadge.getApp())) {
                updatableAppBadge.install.setVisibility(View.VISIBLE);
                updatableAppBadge.cancel.setVisibility(View.GONE);
            } else if (Util.shouldDownload(context, updatableAppBadge.getApp())
                    && !updatableAppBadge.isDownloading) {
                updatableAppBadge.update.setVisibility(View.VISIBLE);
                updatableAppBadge.cancel.setVisibility(View.GONE);
            }
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
