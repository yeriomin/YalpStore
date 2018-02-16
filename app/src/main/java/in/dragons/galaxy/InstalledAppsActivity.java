package in.dragons.galaxy;

import android.app.ActivityOptions;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;

import in.dragons.galaxy.fragment.FilterMenu;
import in.dragons.galaxy.model.App;
import in.dragons.galaxy.task.AppListValidityCheckTask;
import in.dragons.galaxy.task.ForegroundInstalledAppsTask;
import in.dragons.galaxy.view.InstalledAppBadge;
import in.dragons.galaxy.view.ListItem;

public class InstalledAppsActivity extends AppListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        setTheme(sharedPreferences.getBoolean("THEME", true) ? R.style.AppTheme : R.style.AppTheme_Dark);
        super.onCreate(savedInstanceState);
        ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
        setTitle(R.string.activity_title_updates_and_other_apps);
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
    protected ListItem getListItem(App app) {
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
