package in.dragons.galaxy.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.percolate.caffeine.ViewUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.dragons.galaxy.AppListIterator;
import in.dragons.galaxy.PlayStoreApiAuthenticator;
import in.dragons.galaxy.R;
import in.dragons.galaxy.adapters.AppListAdapter;
import in.dragons.galaxy.adapters.ViewPagerAdapter;
import in.dragons.galaxy.model.App;
import in.dragons.galaxy.view.ListItem;
import in.dragons.galaxy.view.ProgressIndicator;
import in.dragons.galaxy.view.SearchResultAppBadge;

public class GalaxyActivity extends BaseActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.search_toolbar)
    SearchView searchToolbar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.sliding_tabs)
    TabLayout slidingTabs;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    protected Map<String, ListItem> listItems = new HashMap<>();
    protected AppListIterator iterator;

    static public App app;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        addQueryTextListener(searchToolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navView.setNavigationItemSelectedListener(this);

        viewPager.setAdapter(new ViewPagerAdapter(this, getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(3);
        slidingTabs.setupWithViewPager(viewPager);

        if (isLoggedIn())
            setUser();

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    public void setUser() {
        View header = navView.getHeaderView(0);
        if (isGoogle() && isConnected())
            ViewUtils.setText(header, R.id.usr_email,
                    sharedPreferences.getString(PlayStoreApiAuthenticator.PREFERENCE_EMAIL, ""));
        else if (isDummy())
            ViewUtils.setText(header, R.id.usr_email, getResources().getString(R.string.header_usr_email));
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
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_myapps:
                startActivity(new Intent(this, GalaxyActivity.class));
                break;
            case R.id.action_settings:
                startActivity(new Intent(this, PreferenceActivity.class));
                break;
            case R.id.action_accounts:
                startActivity(new Intent(this, AccountsActivity.class));
                break;
            case R.id.action_spoofed:
                startActivity(new Intent(this, SpoofActivity.class));
                break;
            case R.id.action_themes:
                startActivity(new Intent(this, ThemesActivity.class));
                break;
            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
        }

        drawerLayout = ViewUtils.findViewById(this, R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
