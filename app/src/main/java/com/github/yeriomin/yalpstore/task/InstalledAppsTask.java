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

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.util.Log;

import com.github.yeriomin.yalpstore.model.App;

import java.util.HashMap;
import java.util.Map;

public class InstalledAppsTask extends TaskWithProgress<Map<String, App>> {

    protected boolean includeSystemApps = false;

    public void setIncludeSystemApps(boolean includeSystemApps) {
        this.includeSystemApps = includeSystemApps;
    }

    static public App getInstalledApp(PackageManager pm, String packageName) {
        try {
            App app = new App(pm.getPackageInfo(packageName, PackageManager.GET_META_DATA | PackageManager.GET_PERMISSIONS));
            try {
                app.setDisplayName(pm.getApplicationLabel(app.getPackageInfo().applicationInfo).toString());
            } catch (Resources.NotFoundException e1) {
                Log.e(InstalledAppsTask.class.getSimpleName(), app.getPackageName() + " apparently has no display name");
            }
            return app;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    static protected Map<String, App> filterSystemApps(Map<String, App> apps) {
        Map<String, App> result = new HashMap<>();
        for (App app: apps.values()) {
            if (!app.isSystem()) {
                result.put(app.getPackageName(), app);
            }
        }
        return result;
    }

    public Map<String, App> getInstalledApps(boolean includeDisabled) {
        Map<String, App> installedApps = new HashMap<>();
        PackageManager pm = context.getPackageManager();
        for (PackageInfo reducedPackageInfo: pm.getInstalledPackages(0)) {
            if (!includeDisabled
                && null != reducedPackageInfo.applicationInfo
                && !reducedPackageInfo.applicationInfo.enabled
            ) {
                continue;
            }
            App app = getInstalledApp(pm, reducedPackageInfo.packageName);
            if (null != app) {
                installedApps.put(app.getPackageName(), app);
            }
        }
        if (!includeSystemApps) {
            installedApps = filterSystemApps(installedApps);
        }
        return installedApps;
    }

    @Override
    protected Map<String, App> doInBackground(String... strings) {
        return getInstalledApps(true);
    }
}
