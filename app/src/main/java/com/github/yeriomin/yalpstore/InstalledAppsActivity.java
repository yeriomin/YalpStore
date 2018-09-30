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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.github.yeriomin.yalpstore.task.OldApkCleanupTask;
import com.github.yeriomin.yalpstore.view.InstalledAppBadge;
import com.github.yeriomin.yalpstore.view.InstalledAppsMainButtonAdapter;
import com.github.yeriomin.yalpstore.view.ListItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

public class InstalledAppsActivity extends AppListActivity {

    static public final String INSTALLED_APPS_LOADED_ACTION = "INSTALLED_APPS_LOADED_ACTION";

    private InstalledAppsBroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.activity_title_updates_and_other_apps);
        new InstalledAppsMainButtonAdapter(findViewById(R.id.main_button)).init();
        new BitmapCacheCleanupTask(this.getApplicationContext()).executeOnExecutorIfPossible();
        new OldApkCleanupTask(this.getApplicationContext()).executeOnExecutorIfPossible();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (YalpStoreApplication.installedPackages.isEmpty()) {
            receiver = new InstalledAppsBroadcastReceiver(this);
            findViewById(R.id.progress).setVisibility(View.VISIBLE);
        } else {
            AppListValidityCheckTask task = new AppListValidityCheckTask(this);
            task.setIncludeSystemApps(new FilterMenu(this).getFilterPreferences().isSystemApps());
            task.execute();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
        receiver = null;
    }

    @Override
    public void loadApps() {
        boolean includeSystemApps = new FilterMenu(this).getFilterPreferences().isSystemApps();
        List<App> installedApps = new ArrayList<>(YalpStoreApplication.installedPackages.values());
        ListIterator<App> iterator = installedApps.listIterator();
        while (iterator.hasNext()){
            if (iterator.next().isSystem() && !includeSystemApps){
                iterator.remove();
            }
        }
        Collections.sort(installedApps);
        clearApps();
        addApps(installedApps);
        findViewById(R.id.progress).setVisibility(View.GONE);
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

    private static class InstalledAppsBroadcastReceiver extends BroadcastReceiver {

        private InstalledAppsActivity activity;

        public InstalledAppsBroadcastReceiver(InstalledAppsActivity activity) {
            super();
            this.activity = activity;
            activity.registerReceiver(this, new IntentFilter(INSTALLED_APPS_LOADED_ACTION));
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            activity.loadApps();
        }
    }
}
