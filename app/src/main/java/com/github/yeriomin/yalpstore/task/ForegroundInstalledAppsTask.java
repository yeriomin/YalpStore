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

import android.os.SystemClock;

import com.github.yeriomin.yalpstore.ContextUtil;
import com.github.yeriomin.yalpstore.InstalledAppsActivity;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.YalpStoreApplication;
import com.github.yeriomin.yalpstore.fragment.FilterMenu;
import com.github.yeriomin.yalpstore.model.App;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class ForegroundInstalledAppsTask extends TaskWithProgress<Map<String, App>> {

    private WeakReference<InstalledAppsActivity> activityRef;

    public ForegroundInstalledAppsTask(InstalledAppsActivity activity) {
        activityRef = new WeakReference<>(activity);
        setContext(activity.getApplicationContext());
        setProgressIndicator(activity.findViewById(R.id.progress));
    }

    @Override
    protected Map<String, App> doInBackground(String... strings) {
        // Installed app list is put into YalpStoreApplication.installedPackages in
        // YalpStoreApplication.onCreate(), so lets just wait for it to finish.
        // The wait happens on app launch only.
        int waitStep = 100;
        int waitLimit = 10000;
        int waited = 0;
        while (YalpStoreApplication.installedPackages.isEmpty() && waited <= waitLimit) {
            SystemClock.sleep(waitStep);
            waited += waitStep;
        }
        return YalpStoreApplication.installedPackages;
    }

    @Override
    protected void onPostExecute(Map<String, App> appMap) {
        super.onPostExecute(appMap);
        if (null == activityRef.get() || !ContextUtil.isAlive(activityRef.get())) {
            return;
        }
        boolean includeSystemApps = new FilterMenu(activityRef.get()).getFilterPreferences().isSystemApps();
        List<App> installedApps = new ArrayList<>(appMap.values());
        ListIterator<App> iterator = installedApps.listIterator();
        while (iterator.hasNext()){
            if (!includeSystemApps && iterator.next().isSystem()){
                iterator.remove();
            }
        }
        Collections.sort(installedApps);
        activityRef.get().clearApps();
        activityRef.get().addApps(installedApps);
    }
}
