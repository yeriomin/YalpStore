/*
 * Yalp Store
 * Copyright (C) 2018 Sergey Yeriomin <yeriomin@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.github.yeriomin.yalpstore.task;

import android.os.AsyncTask;

import com.github.yeriomin.yalpstore.AppListActivity;
import com.github.yeriomin.yalpstore.BlackWhiteListManager;
import com.github.yeriomin.yalpstore.YalpStoreApplication;
import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.view.AppBadge;

import java.util.HashMap;
import java.util.HashSet;
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
        if (YalpStoreApplication.installedPackages.isEmpty()) {
            return;
        }
        if (!installedPackageNames.isEmpty() && activity.getListedPackageNames().isEmpty()) {
            activity.loadApps();
            return;
        }
        Set<String> newPackageNames = new HashSet<>(installedPackageNames.keySet());
        newPackageNames.removeAll(activity.getListedPackageNames());
        if (!respectUpdateBlacklist && newPackageNames.size() > 0) {
            activity.loadApps();
            return;
        }
        Set<String> packagesToRemove = new HashSet<>();
        packagesToRemove.addAll(getRemovedPackageNames(installedPackageNames.keySet()));
        if (respectUpdateBlacklist) {
            packagesToRemove.addAll(getUpdatedPackageNames(installedPackageNames));
        }
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
        BlackWhiteListManager manager = new BlackWhiteListManager(activity);
        for (App app: YalpStoreApplication.installedPackages.values()) {
            if (!includeSystemApps && app.isSystem()) {
                continue;
            }
            if (respectUpdateBlacklist && !manager.isUpdatable(app.getPackageName())) {
                continue;
            }
            installedApps.put(app.getPackageName(), app.getVersionCode());
        }
        return installedApps;
    }
}
