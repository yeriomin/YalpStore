package com.dragons.aurora.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.dragons.aurora.playstoreapiv2.GooglePlayAPI;

import com.dragons.aurora.BlackWhiteListManager;
import com.dragons.aurora.AuroraApplication;
import com.dragons.aurora.AuroraPermissionManager;
import com.dragons.aurora.R;
import com.dragons.aurora.UpdateAllReceiver;
import com.dragons.aurora.UpdateChecker;
import com.dragons.aurora.model.App;
import com.dragons.aurora.task.AppListValidityCheckTask;
import com.dragons.aurora.task.playstore.ForegroundUpdatableAppsTask;
import com.dragons.aurora.view.ListItem;
import com.dragons.aurora.view.UpdatableAppBadge;

public class UpdatableAppsActivity extends AppListActivity {

    private UpdateAllReceiver updateAllReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.activity_title_updates_only));
        onNewIntent(getIntent());

        TextView delta = (TextView) findViewById(R.id.updates_setting);
        delta.setText(sharedPreferences.getBoolean("PREFERENCE_DOWNLOAD_DELTAS", true) ? R.string.delta_enabled : R.string.delta_disabled);
        delta.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateAllReceiver = new UpdateAllReceiver(this);
        AppListValidityCheckTask task = new AppListValidityCheckTask(this);
        task.setRespectUpdateBlacklist(true);
        task.setIncludeSystemApps(true);
        task.execute();
        //fetchDetails();
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
        //fetchDetails();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (AuroraPermissionManager.isGranted(requestCode, permissions, grantResults)) {
            Log.i(getClass().getSimpleName(), "User granted the write permission");
            launchUpdateAll();
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (item.getItemId() == R.id.action_ignore || item.getItemId() == R.id.action_unwhitelist) {
            String packageName = getAppByListPosition(info.position).getPackageName();
            BlackWhiteListManager manager = new BlackWhiteListManager(this);
            if (item.getItemId() == R.id.action_ignore) {
                manager.add(packageName);
            } else {
                manager.remove(packageName);
            }
            removeApp(packageName);
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected ListItem getListItem(App app) {
        UpdatableAppBadge appBadge = new UpdatableAppBadge();
        appBadge.setApp(app);
        return appBadge;
    }

    @Override
    public void removeApp(String packageName) {
        super.removeApp(packageName);
        if (listItems.isEmpty()) {
            findViewById(R.id.unicorn).setVisibility(View.VISIBLE);
        }
    }

    private ForegroundUpdatableAppsTask getTask() {
        ForegroundUpdatableAppsTask task = new ForegroundUpdatableAppsTask(this);
        task.setErrorView((TextView) getListView().getEmptyView());
        if (listItems.isEmpty())
            task.setProgressIndicator(findViewById(R.id.progress));
        return task;
    }

    public void launchUpdateAll() {
        ((AuroraApplication) getApplicationContext()).setBackgroundUpdating(true);
        new UpdateChecker().onReceive(UpdatableAppsActivity.this, getIntent());
        findViewById(R.id.update_all).setVisibility(View.GONE);
        Button button = (Button) findViewById(R.id.update_cancel);
        button.setVisibility(View.VISIBLE);
    }
}

