package in.dragons.galaxy.fragment;

import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.percolate.caffeine.ViewUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.dragons.galaxy.R;
import in.dragons.galaxy.activities.DetailsActivity;
import in.dragons.galaxy.activities.GalaxyActivity;
import in.dragons.galaxy.adapters.AppListAdapter;
import in.dragons.galaxy.fragment.details.ButtonDownload;
import in.dragons.galaxy.fragment.details.ButtonUninstall;
import in.dragons.galaxy.fragment.details.DownloadOptions;
import in.dragons.galaxy.model.App;
import in.dragons.galaxy.view.AppBadge;
import in.dragons.galaxy.view.InstalledAppBadge;
import in.dragons.galaxy.view.ListItem;

abstract public class AppListFragment extends UtilFragment {

    protected ListView listView;
    protected Map<String, ListItem> listItems = new HashMap<>();
    protected View v;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        DetailsActivity.app = getAppByListPosition(info.position);
        new DownloadOptions((GalaxyActivity) this.getActivity(), DetailsActivity.app).inflate(menu);
        menu.findItem(R.id.action_download).setVisible(new ButtonDownload((GalaxyActivity) this.getActivity(), DetailsActivity.app).shouldBeVisible());
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
                new DownloadOptions((GalaxyActivity) this.getActivity(), DetailsActivity.app).onContextItemSelected(item);
                ((ListItem) getListView().getItemAtPosition(info.position)).draw();
                break;
            case R.id.action_download:
                new ButtonDownload((GalaxyActivity) this.getActivity(), DetailsActivity.app).checkAndDownload();
                break;
            case R.id.action_uninstall:
                new ButtonUninstall((GalaxyActivity) this.getActivity(), DetailsActivity.app).uninstall();
                break;
            default:
                return new DownloadOptions((GalaxyActivity) this.getActivity(), DetailsActivity.app).onContextItemSelected(item);
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

    protected void setProgress() {
        ViewUtils.findViewById(this.getActivity(), R.id.progress).setVisibility(View.VISIBLE);
    }

    protected void removeProgress() {
        ViewUtils.findViewById(this.getActivity(), R.id.progress).setVisibility(View.GONE);
    }

    protected ListView getListView() {
        return listView;
    }

    protected void setText(int viewId, String text) {
        TextView textView = ViewUtils.findViewById(this.getActivity(), viewId);
        if (null != textView)
            textView.setText(text);
    }

    protected void setText(int viewId, int stringId, Object... text) {
        setText(viewId, this.getString(stringId, text));
    }

    protected boolean isLoggedIn() {
        return PreferenceFragment.getBoolean(getActivity(), "LOGGED_IN");
    }

    protected boolean isDummy() {
        return PreferenceFragment.getBoolean(getActivity(), "DUMMY_ACC");
    }

    protected boolean isGoogle() {
        return PreferenceFragment.getBoolean(getActivity(), "GOOGLE_ACC");
    }
}
