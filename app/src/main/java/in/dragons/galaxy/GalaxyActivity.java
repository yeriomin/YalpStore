package in.dragons.galaxy;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.aesthetic.Aesthetic;
import com.afollestad.aesthetic.NavigationViewMode;
import com.percolate.caffeine.ViewUtils;

import in.dragons.galaxy.fragment.FilterMenu;

public abstract class GalaxyActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (Aesthetic.isFirstTime()) {
            Aesthetic.get()
                    .activityTheme(R.style.AppTheme)
                    .textColorPrimaryRes(R.color.colorTextPrimary)
                    .textColorSecondaryRes(R.color.colorTextSecondary)
                    .colorPrimaryRes(R.color.colorPrimary)
                    .colorAccentRes(R.color.colorAccent)
                    .colorStatusBarAuto()
                    .colorNavigationBarAuto()
                    .textColorPrimary(Color.BLACK)
                    .navigationViewMode(NavigationViewMode.SELECTED_ACCENT)
                    .apply();
        }

        getUser();

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    public void getUser() {
        View header = navigationView.getHeaderView(0);
        if (isValidEmail(Email) && isConnected())
            new GoogleAccountInfo(Email) {
                @Override
                public void onPostExecute(String result) {
                    parseRAW(result);
                }
            }.execute();
        else if (isDummyEmail())
            ViewUtils.setText(header, R.id.usr_email, getResources().getString(R.string.header_usr_email));
    }

    @Override
    protected void onResume() {
        Log.v(getClass().getSimpleName(), "Resuming activity");
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            invalidateOptionsMenu();
        }
        if (logout) {
            finish();
        }
    }

    @Override
    protected void onPause() {
        Log.v(getClass().getSimpleName(), "Pausing activity");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.v(getClass().getSimpleName(), "Stopping activity");
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        new FilterMenu(this).onCreateOptionsMenu(menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter_system_apps:
            case R.id.filter_apps_with_ads:
            case R.id.filter_paid_apps:
            case R.id.filter_gsf_dependent_apps:
            case R.id.filter_category:
            case R.id.filter_rating:
            case R.id.filter_downloads:
                new FilterMenu(this).onOptionsItemSelected(item);
                break;
        }
        return super.onOptionsItemSelected(item);
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

    protected void finishAll() {
        logout = true;
        finish();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_myapps:
                startActivity(new Intent(this, InstalledAppsActivity.class));
                break;
            case R.id.action_updates:
                startActivity(new Intent(this, UpdatableAppsActivity.class));
                break;
            case R.id.action_categories:
                startActivity(new Intent(this, CategoryListActivity.class));
                break;
            case R.id.action_settings:
                startActivity(new Intent(this, PreferenceActivity.class));
                break;
            case R.id.action_accounts:
                startActivity(new Intent(this, AccountsActivity.class));
                break;
            case R.id.action_themes:
                startActivity(new Intent(this, ThemesActivity.class));
                break;
            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
