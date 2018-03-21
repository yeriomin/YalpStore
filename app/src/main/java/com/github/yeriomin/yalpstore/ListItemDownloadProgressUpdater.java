package com.github.yeriomin.yalpstore;

import com.github.yeriomin.yalpstore.view.AppBadge;

public class ListItemDownloadProgressUpdater extends DownloadProgressUpdater {

    private AppBadge appBadge;

    public ListItemDownloadProgressUpdater(String packageName, AppBadge appBadge) {
        super(packageName);
        this.appBadge = appBadge;
    }

    @Override
    protected void setProgress(int progress, int max) {
        appBadge.setProgress(progress, max);
    }

    @Override
    protected void finish() {
        appBadge.hideMoreButton();
    }
}
