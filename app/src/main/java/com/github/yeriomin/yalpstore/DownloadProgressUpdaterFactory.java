package com.github.yeriomin.yalpstore;

import android.widget.ProgressBar;

import com.github.yeriomin.yalpstore.view.AppBadge;

public class DownloadProgressUpdaterFactory {

    static public DownloadProgressUpdater get(YalpStoreActivity activity, String packageName) {
        if (activity instanceof DetailsActivity) {
            return get((DetailsActivity) activity, packageName);
        } else if (activity instanceof AppListActivity) {
            return get((AppListActivity) activity, packageName);
        }
        return null;
    }

    static private DownloadProgressUpdater get(DetailsActivity activity, String packageName) {
        return new DownloadProgressBarUpdater(packageName, (ProgressBar) activity.findViewById(R.id.download_progress));
    }

    static private DownloadProgressUpdater get(AppListActivity activity, String packageName) {
        return new ListItemDownloadProgressUpdater(packageName, (AppBadge) activity.getListItem(packageName));
    }
}
