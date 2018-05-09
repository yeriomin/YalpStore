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

package com.github.yeriomin.yalpstore;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;

import com.github.yeriomin.yalpstore.fragment.FilterMenu;
import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.task.AppListValidityCheckTask;
import com.github.yeriomin.yalpstore.task.BitmapCacheCleanupTask;
import com.github.yeriomin.yalpstore.task.ForegroundInstalledAppsTask;
import com.github.yeriomin.yalpstore.task.OldApkCleanupTask;
import com.github.yeriomin.yalpstore.view.InstalledAppBadge;
import com.github.yeriomin.yalpstore.view.InstalledAppsMainButtonAdapter;
import com.github.yeriomin.yalpstore.view.ListItem;

public class InstalledAppsActivity extends AppListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.activity_title_updates_and_other_apps);
        new InstalledAppsMainButtonAdapter(findViewById(R.id.main_button)).init();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            new BitmapCacheCleanupTask(this.getApplicationContext()).execute();
            new OldApkCleanupTask(this.getApplicationContext()).execute();
        } else {
            new BitmapCacheCleanupTask(this.getApplicationContext()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            new OldApkCleanupTask(this.getApplicationContext()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppListValidityCheckTask task = new AppListValidityCheckTask(this);
        task.setIncludeSystemApps(new FilterMenu(this).getFilterPreferences().isSystemApps());
        task.execute();
    }

    @Override
    public void loadApps() {
        new ForegroundInstalledAppsTask(this).execute();
    }

    @Override
    protected ListItem buildListItem(App app) {
        InstalledAppBadge appBadge = new InstalledAppBadge();
        appBadge.setApp(app);
        return appBadge;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.findItem(R.id.action_filter).setVisible(true);
        menu.findItem(R.id.filter_system_apps).setVisible(true);
        return result;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.findItem(R.id.action_flag).setVisible(false);
    }
}
