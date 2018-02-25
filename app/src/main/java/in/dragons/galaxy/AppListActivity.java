package in.dragons.galaxy;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.percolate.caffeine.ViewUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import in.dragons.galaxy.fragment.details.ButtonDownload;
import in.dragons.galaxy.fragment.details.ButtonUninstall;
import in.dragons.galaxy.fragment.details.DownloadOptions;
import in.dragons.galaxy.model.App;
import in.dragons.galaxy.view.AppBadge;
import in.dragons.galaxy.view.ListItem;

abstract public class AppListActivity extends GalaxyActivity {

    protected ListView listView;
    protected Map<String, ListItem> listItems = new HashMap<>();
    protected String Email;

    abstract public void loadApps();

    abstract protected ListItem getListItem(App app);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame); //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.applist_activity_layout, contentFrameLayout);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (!onSearchRequested()) {
                    showFallbackSearchDialog();
                }
            }
        });

        onContentChange();

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DetailsActivity.app = getAppByListPosition(position);
                startActivity(DetailsActivity.getDetailsIntent(AppListActivity.this, DetailsActivity.app.getPackageName()));
            }
        });
        registerForContextMenu(getListView());
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
            case R.id.action_unignore:
            case R.id.action_unwhitelist:
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
        if (GalaxyPermissionManager.isGranted(requestCode, permissions, grantResults)) {
            Log.i(getClass().getSimpleName(), "User granted the write permission");
            new ButtonDownload(this, DetailsActivity.app).download();
        }
    }


    public void onContentChange() {
        View emptyView = findViewById(android.R.id.empty);
        listView = ViewUtils.findViewById(this, android.R.id.list);
        listView.setNestedScrollingEnabled(true);
        if (emptyView != null) {
            listView.setEmptyView(emptyView);
        }
        if (null == listView.getAdapter()) {
            listView.setAdapter(new AppListAdapter(this, R.layout.two_line_list_item_with_icon));
        }
    }

    protected App getAppByListPosition(int position) {
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
        for (App app : appsToAdd) {
            ListItem listItem = getListItem(app);
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
