package com.github.yeriomin.yalpstore.task;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;

import com.github.yeriomin.yalpstore.AppListActivity;
import com.github.yeriomin.yalpstore.BlackWhiteListManager;
import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.view.AppBadge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AppListValidityCheckTask extends AsyncTask<String, Void, Map<String, Integer>> {

    private AppListActivity activity;
    protected boolean includeSystemApps = false;
    protected boolean respectUpdateBlacklist = false;

    public void setIncludeSystemApps(boolean includeSystemApps) {
        this.includeSystemApps = includeSystemApps;
    }

    public void setRespectUpdateBlacklist(boolean respectUpdateBlacklist) {
        this.respectUpdateBlacklist = respectUpdateBlacklist;
    }

    public AppListValidityCheckTask(AppListActivity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPostExecute(Map<String, Integer> installedPackageNames) {
        super.onPostExecute(installedPackageNames);
        Set<String> newPackageNames = new HashSet<>(installedPackageNames.keySet());
        newPackageNames.removeAll(activity.getListedPackageNames());
        if (!respectUpdateBlacklist && newPackageNames.size() > 0) {
            activity.loadApps();
            return;
        }
        Set<String> packagesToRemove = new HashSet<>();
        packagesToRemove.addAll(getRemovedPackageNames(installedPackageNames.keySet()));
        packagesToRemove.addAll(getUpdatedPackageNames(installedPackageNames));
        for (String packageName: packagesToRemove) {
            activity.removeApp(packageName);
        }
    }

    private Set<String> getRemovedPackageNames(Set<String> installedPackageNames) {
        Set<String> removedPackageNames = new HashSet<>(activity.getListedPackageNames());
        removedPackageNames.removeAll(installedPackageNames);
        return removedPackageNames;
    }

    private Set<String> getUpdatedPackageNames(Map<String, Integer> installedPackages) {
        Set<String> updatedPackageNames = new HashSet<>();
        for (String packageName: installedPackages.keySet()) {
            if (null != activity.getListItem(packageName)) {
                App app = ((AppBadge) activity.getListItem(packageName)).getApp();
                if (app.getVersionCode() == installedPackages.get(packageName)) {
                    updatedPackageNames.add(packageName);
                }
            }
        }
        return updatedPackageNames;
    }

    @Override
    protected Map<String, Integer> doInBackground(String... strings) {
        Map<String, Integer> installedApps = new HashMap<>();
        List<PackageInfo> installedPackages = new ArrayList<>();
        try {
            installedPackages.addAll(activity.getPackageManager().getInstalledPackages(0));
        } catch (RuntimeException e) {
            // Sometimes TransactionTooLargeException is thrown even though getInstalledPackages is
            // called with 0 flags. App list validity check is not essential, so this can be ignored
            // TODO: There might be a way to avoid this exception, although I doubt it
        }
        BlackWhiteListManager manager = new BlackWhiteListManager(activity);
        for (PackageInfo reducedPackageInfo: installedPackages) {
            if (!includeSystemApps
                && null != reducedPackageInfo.applicationInfo
                && (reducedPackageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0
            ) {
                continue;
            }
            if (respectUpdateBlacklist && !manager.isUpdatable(reducedPackageInfo.packageName)) {
                continue;
            }
            installedApps.put(reducedPackageInfo.packageName, reducedPackageInfo.versionCode);
        }
        return installedApps;
    }
}
