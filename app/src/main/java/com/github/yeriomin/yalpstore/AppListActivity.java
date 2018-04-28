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

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.fragment.details.ButtonDownload;
import com.github.yeriomin.yalpstore.fragment.details.ButtonUninstall;
import com.github.yeriomin.yalpstore.fragment.details.DownloadOptions;
import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.view.AppBadge;
import com.github.yeriomin.yalpstore.view.ListItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

abstract public class AppListActivity extends YalpStoreActivity {

    protected ListView listView;
    protected Map<String, ListItem> listItems = new HashMap<>();
    protected AppListDownloadReceiver appListDownloadReceiver;

    abstract public void loadApps();
    abstract protected ListItem buildListItem(App app);

    public ListItem getListItem(String packageName) {
        return listItems.get(packageName);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.applist_activity_layout);

        onContentChanged();
        getListView().setOnItemClickListener(new OnAppClickListener(this));
        registerForContextMenu(getListView());
    }

    @Override
    protected void onPause() {
        unregisterReceiver(appListDownloadReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        appListDownloadReceiver = new AppListDownloadReceiver(this);
        super.onResume();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        DetailsActivity.app = getAppByListPosition(info.position);
        new DownloadOptions(this, DetailsActivity.app).inflate(menu);
        menu.findItem(R.id.action_download).setVisible(new ButtonDownload(this, DetailsActivity.app).shouldBeVisible());
        menu.findItem(R.id.action_uninstall).setVisible(DetailsActivity.app.isInstalled());
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        DetailsActivity.app = getAppByListPosition(info.position);
        switch (item.getItemId()) {
            case R.id.action_ignore:
            case R.id.action_whitelist:
                new DownloadOptions(this, DetailsActivity.app).onContextItemSelected(item);
                ((ListItem) getListView().getItemAtPosition(info.position)).draw();
                break;
            case R.id.action_download:
                new ButtonDownload(this, DetailsActivity.app).checkAndDownload();
                break;
            case R.id.action_uninstall:
                new ButtonUninstall(this, DetailsActivity.app).uninstall();
                break;
            default:
                return new DownloadOptions(this, DetailsActivity.app).onContextItemSelected(item);
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (YalpStorePermissionManager.isGranted(requestCode, permissions, grantResults)) {
            new ButtonDownload(this, DetailsActivity.app).download();
        }
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        View emptyView = findViewById(android.R.id.empty);
        listView = findViewById(android.R.id.list);
        if (null == listView) {
            return;
        }
        if (emptyView != null) {
            listView.setEmptyView(emptyView);
        }
        if (null == listView.getAdapter()) {
            listView.setAdapter(new AppListAdapter(this, R.layout.two_line_list_item_with_icon));
        }
    }

    public App getAppByListPosition(int position) {
        ListItem listItem = (ListItem) getListView().getItemAtPosition(position);
        if (null == listItem || !(listItem instanceof AppBadge)) {
            return null;
        }
        return ((AppBadge) listItem).getApp();
    }

    public void addApps(List<App> appsToAdd) {
        addApps(appsToAdd, true);
    }

    public void addApps(List<App> appsToAdd, boolean update) {
        AppListAdapter adapter = (AppListAdapter) getListView().getAdapter();
        adapter.setNotifyOnChange(false);
        for (App app: appsToAdd) {
            ListItem listItem = buildListItem(app);
            listItems.put(app.getPackageName(), listItem);
            adapter.add(listItem);
        }
        if (update) {
            adapter.notifyDataSetChanged();
        }
    }

    public void removeApp(String packageName) {
        ((AppListAdapter) getListView().getAdapter()).remove(listItems.get(packageName));
        listItems.remove(packageName);
        if (listItems.isEmpty()) {
            ((TextView) getListView().getEmptyView()).setText(R.string.list_empty_search);
        }
    }

    public Set<String> getListedPackageNames() {
        return listItems.keySet();
    }

    public void clearApps() {
        listItems.clear();
        ((AppListAdapter) getListView().getAdapter()).clear();
    }

    public ListView getListView() {
        return listView;
    }
}
