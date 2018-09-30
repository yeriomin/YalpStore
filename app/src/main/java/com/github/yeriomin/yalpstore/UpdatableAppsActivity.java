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

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.download.DownloadManager;
import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.task.AppListValidityCheckTask;
import com.github.yeriomin.yalpstore.task.playstore.ForegroundUpdatableAppsTask;
import com.github.yeriomin.yalpstore.view.ListItem;
import com.github.yeriomin.yalpstore.view.UpdatableAppBadge;
import com.github.yeriomin.yalpstore.view.UpdatableAppsButtonAdapter;

public class UpdatableAppsActivity extends AppListActivity {

    public static final int REQUEST_CODE_UPDATE_ALL = 106;

    private UpdateAllReceiver updateAllReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.activity_title_updates_only));
        onNewIntent(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateAllReceiver = new UpdateAllReceiver(this);
        AppListValidityCheckTask task = new AppListValidityCheckTask(this);
        task.setRespectUpdateBlacklist(true);
        task.setIncludeSystemApps(true);
        task.execute();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(updateAllReceiver);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
    }

    @Override
    public void loadApps() {
        getTask().execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == REQUEST_CODE_UPDATE_ALL) {
            if (YalpStorePermissionManager.isGranted(requestCode, permissions, grantResults)) {
                launchUpdateAll();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        App app = getAppByListPosition(info.position);
        if (item.getItemId() == R.id.action_ignore) {
            String packageName = app.getPackageName();
            new BlackWhiteListManager(this).add(packageName);
            removeApp(packageName);
            return true;
        } else if (item.getItemId() == R.id.action_ignore_this) {
            new VersionIgnoreManager(this).add(app.getPackageName(), app.getVersionCode());
            removeApp(app.getPackageName());
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected ListItem buildListItem(App app) {
        UpdatableAppBadge appBadge = new UpdatableAppBadge();
        appBadge.setApp(app);
        return appBadge;
    }

    @Override
    public void removeApp(String packageName) {
        super.removeApp(packageName);
        if (listItems.isEmpty()) {
            ((TextView) getListView().getEmptyView()).setText(R.string.list_empty_updates);
            findViewById(R.id.main_button).setVisibility(View.GONE);
        }
    }

    private ForegroundUpdatableAppsTask getTask() {
        ForegroundUpdatableAppsTask task = new ForegroundUpdatableAppsTask(this);
        task.setErrorView((TextView) getListView().getEmptyView());
        task.setProgressIndicator(findViewById(R.id.progress));
        return task;
    }

    public void launchUpdateAll() {
        ((YalpStoreApplication) getApplicationContext()).setBackgroundUpdating(true);
        for (String packageName: getListedPackageNames()) {
            DownloadManager.unsetCancelled(packageName);
        }
        new UpdateChecker().onReceive(UpdatableAppsActivity.this, getIntent());
        new UpdatableAppsButtonAdapter(findViewById(R.id.main_button)).setUpdating();
    }
}

