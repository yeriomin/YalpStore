package com.github.yeriomin.yalpstore.task;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.github.yeriomin.yalpstore.AppListActivity;
import com.github.yeriomin.yalpstore.BlackWhiteListManager;

import java.util.HashSet;
import java.util.Set;

public class AppListValidityCheckTask extends AsyncTask<String, Void, Set<String>> {

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
    protected void onPostExecute(Set<String> installedPackageNames) {
        super.onPostExecute(installedPackageNames);
        Set<String> newPackageNames = new HashSet<>(installedPackageNames);
        newPackageNames.removeAll(activity.getListedPackageNames());
        if (newPackageNames.size() > 0) {
            Log.i(getClass().getName(), newPackageNames.size() + " new packages not found in list. Rebuilding.");
            activity.loadApps();
            return;
        }
        Set<String> removedPackageNames = new HashSet<>(activity.getListedPackageNames());
        removedPackageNames.removeAll(installedPackageNames);
        for (String packageName: removedPackageNames) {
            Log.i(getClass().getName(), "Removing package from list: " + packageName);
            activity.removeApp(packageName);
        }
    }

    @Override
    protected Set<String> doInBackground(String... strings) {
        Set<String> installedApps = new HashSet<>();
        BlackWhiteListManager manager = new BlackWhiteListManager(activity);
        for (PackageInfo reducedPackageInfo: activity.getPackageManager().getInstalledPackages(0)) {
            if (!includeSystemApps
                && null != reducedPackageInfo.applicationInfo
                && (reducedPackageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0
            ) {
                continue;
            }
            if (respectUpdateBlacklist && !manager.isUpdatable(reducedPackageInfo.packageName)) {
                continue;
            }
            installedApps.add(reducedPackageInfo.packageName);
        }
        return installedApps;
    }
}
