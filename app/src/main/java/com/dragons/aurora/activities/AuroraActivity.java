package com.dragons.aurora.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ListView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.dragons.custom.CustomAppBar;
import com.dragons.aurora.AppListIterator;
import com.dragons.aurora.R;
import com.dragons.aurora.adapters.AppListAdapter;
import com.dragons.aurora.adapters.ViewPagerAdapter;
import com.dragons.aurora.model.App;
import com.dragons.aurora.view.ListItem;
import com.dragons.aurora.view.ProgressIndicator;
import com.dragons.aurora.view.SearchResultAppBadge;

public class AuroraActivity extends BaseActivity implements View.OnClickListener {

    static public App app;
    protected Map<String, ListItem> listItems = new HashMap<>();
    protected AppListIterator iterator;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.bottom_bar)
    CustomAppBar bottm_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        bottm_bar.setNavigationMenu(R.menu.main_menu, this);
        bottm_bar.setSecondaryMenu(R.menu.nav_menu, this);
        bottm_bar.setBlurRadius(10);
        viewPager.setAdapter(new ViewPagerAdapter(this, getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(3);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        if (logout) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void unregisterReceiver(BroadcastReceiver receiver) {
        if (null == receiver) {
            return;
        }
        try {
            super.unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
            // Ignoring
        }
    }

    @Override
    public Set<String> getListedPackageNames() {
        return listItems.keySet();
    }

    @Override
    public void redrawDetails(App app) {
    }

    @Override
    public ListView getListView() {
        return (ListView) this.getSupportFragmentManager()
                .findFragmentById(R.id.content_frame)
                .getView()
                .findViewById(android.R.id.list);
    }

    @Override
    protected ListItem getListItem(App app) {
        SearchResultAppBadge appBadge = new SearchResultAppBadge();
        appBadge.setApp(app);
        return appBadge;
    }

    @Override
    public void addApps(List<App> appsToAdd) {
        AppListAdapter adapter = (AppListAdapter) getListView().getAdapter();
        if (!adapter.isEmpty()) {
            ListItem last = adapter.getItem(adapter.getCount() - 1);
            if (last instanceof ProgressIndicator) {
                adapter.remove(last);
            }
        }
        super.addApps(appsToAdd, false);
        if (!appsToAdd.isEmpty()) {
            adapter.add(new ProgressIndicator());
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void clearApps() {
        super.clearApps();
        iterator = null;
    }

    public void loadInstalledApps() {
    }

    @Override
    public void onClick(View v) {
        switch ((int) v.getTag()) {
            case R.id.action_home:
                viewPager.setCurrentItem(0, true);
                break;
            case R.id.action_updates:
                viewPager.setCurrentItem(1, true);
                break;
            case R.id.action_categories:
                viewPager.setCurrentItem(2, true);
                break;
            case R.id.action_search:
                viewPager.setCurrentItem(3, true);
                break;
            case R.id.action_accounts:
                startActivity(new Intent(getApplicationContext(), AccountsActivity.class));
                break;
            case R.id.action_about:
                startActivity(new Intent(getApplicationContext(), AboutActivity.class));
                break;
            case R.id.action_settings:
                startActivity(new Intent(getApplicationContext(), PreferenceActivity.class));
                break;
        }
    }
}
