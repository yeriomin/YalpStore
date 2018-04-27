package com.dragons.aurora.fragment;

import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dragons.aurora.R;
import com.dragons.aurora.activities.AuroraActivity;
import com.dragons.aurora.activities.DetailsActivity;
import com.dragons.aurora.adapters.AppListAdapter;
import com.dragons.aurora.fragment.details.ButtonDownload;
import com.dragons.aurora.fragment.details.ButtonUninstall;
import com.dragons.aurora.fragment.details.DownloadOptions;
import com.dragons.aurora.model.App;
import com.dragons.aurora.view.AppBadge;
import com.dragons.aurora.view.InstalledAppBadge;
import com.dragons.aurora.view.ListItem;
import com.percolate.caffeine.ViewUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract public class AppListFragment extends UtilFragment {

    protected ListView listView;
    protected Map<String, ListItem> listItems = new HashMap<>();
    protected View v;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        DetailsActivity.app = getAppByListPosition(info.position);
        new DownloadOptions((AuroraActivity) this.getActivity(), DetailsActivity.app).inflate(menu);
        menu.findItem(R.id.action_download).setVisible(new ButtonDownload((AuroraActivity) this.getActivity(), DetailsActivity.app).shouldBeVisible());
        menu.findItem(R.id.action_uninstall).setVisible(DetailsActivity.app.isInstalled());
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        DetailsActivity.app = getAppByListPosition(info.position);
        switch (item.getItemId()) {
            case R.id.action_ignore:
            case R.id.action_whitelist:
            case R.id.action_unignore:
            case R.id.action_unwhitelist:
                new DownloadOptions((AuroraActivity) this.getActivity(), DetailsActivity.app).onContextItemSelected(item);
                ((ListItem) getListView().getItemAtPosition(info.position)).draw();
                break;
            case R.id.action_download:
                new ButtonDownload((AuroraActivity) this.getActivity(), DetailsActivity.app).checkAndDownload();
                break;
            case R.id.action_uninstall:
                new ButtonUninstall((AuroraActivity) this.getActivity(), DetailsActivity.app).uninstall();
                break;
            default:
                return new DownloadOptions((AuroraActivity) this.getActivity(), DetailsActivity.app).onContextItemSelected(item);
        }
        return true;
    }

    protected void setupListView(View v, int layoutId) {
        View emptyView = v.findViewById(android.R.id.empty);
        listView = ViewUtils.findViewById(v, android.R.id.list);
        listView.setNestedScrollingEnabled(true);
        if (emptyView != null) {
            listView.setEmptyView(emptyView);
        }
        if (null == listView.getAdapter()) {
            listView.setAdapter(new AppListAdapter(getActivity(), layoutId));
        }
    }

    protected ListItem getListItem(App app) {
        InstalledAppBadge appBadge = new InstalledAppBadge();
        appBadge.setApp(app);
        return appBadge;
    }

    protected void grabDetails(int position) {
        DetailsActivity.app = getAppByListPosition(position);
        startActivity(DetailsActivity.getDetailsIntent(this.getActivity(), DetailsActivity.app.getPackageName()));
    }

    protected App getAppByListPosition(int position) {
        ListItem listItem = (ListItem) getListView().getItemAtPosition(position);
        if (null == listItem || !(listItem instanceof AppBadge)) {
            return null;
        }
        return ((AppBadge) listItem).getApp();
    }

    protected void addApps(List<App> appsToAdd) {
        addApps(appsToAdd, true);
    }

    protected void addApps(List<App> appsToAdd, boolean update) {
        AppListAdapter adapter = (AppListAdapter) getListView().getAdapter();
        adapter.setNotifyOnChange(false);
        for (App app : appsToAdd) {
            ListItem listItem = getListItem(app);
            listItems.put(app.getPackageName(), listItem);
            adapter.add(listItem);
        }
        if (update) {
            adapter.notifyDataSetChanged();
        }
    }

    protected void removeApp(String packageName) {
        ((AppListAdapter) getListView().getAdapter()).remove(listItems.get(packageName));
        listItems.remove(packageName);
    }

    protected void clearApps() {
        listItems.clear();
        ((AppListAdapter) getListView().getAdapter()).clear();
    }

    protected ListView getListView() {
        return listView;
    }
}
