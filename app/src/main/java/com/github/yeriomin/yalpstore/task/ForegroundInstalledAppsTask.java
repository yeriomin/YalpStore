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

import com.github.yeriomin.yalpstore.InstalledAppsActivity;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.fragment.FilterMenu;
import com.github.yeriomin.yalpstore.model.App;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ForegroundInstalledAppsTask extends InstalledAppsTask {

    private InstalledAppsActivity activity;

    public ForegroundInstalledAppsTask(InstalledAppsActivity activity) {
        this.activity = activity;
        setContext(activity.getApplicationContext());
        setProgressIndicator(activity.findViewById(R.id.progress));
        setIncludeSystemApps(new FilterMenu(activity).getFilterPreferences().isSystemApps());
    }

    @Override
    protected void onPostExecute(Map<String, App> result) {
        super.onPostExecute(result);
        activity.clearApps();
        List<App> installedApps = new ArrayList<>(result.values());
        Collections.sort(installedApps);
        activity.addApps(installedApps);
    }
}
