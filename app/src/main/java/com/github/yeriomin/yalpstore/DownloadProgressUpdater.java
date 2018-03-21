package com.github.yeriomin.yalpstore;

import android.util.Pair;

abstract public class DownloadProgressUpdater extends RepeatingTask {

    private String packageName;

    abstract protected void setProgress(int progress, int max);
    abstract protected void finish();

    public DownloadProgressUpdater(String packageName) {
        this.packageName = packageName;
    }

    @Override
    protected boolean shouldRunAgain() {
        DownloadState state = DownloadState.get(packageName);
        return null != state && !state.isEverythingFinished();
    }

    @Override
    protected void payload() {
        DownloadState state = DownloadState.get(packageName);
        if (null == state || state.isEverythingFinished()) {
            finish();
        } else {
            Pair<Integer, Integer> progress = state.getProgress();
            setProgress(progress.first, progress.second);
        }
    }
}
