package com.github.yeriomin.yalpstore;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.view.ListItem;
import com.github.yeriomin.yalpstore.view.UpdatableAppBadge;

import java.util.List;

public class UpdatableAppsActivity extends AppListActivity {

    static private boolean needsUpdate;

    private UpdateAllReceiver updateAllReceiver;

    static public void setNeedsUpdate(boolean needsUpdate) {
        UpdatableAppsActivity.needsUpdate = needsUpdate;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setNeedsUpdate(true);
        ((TextView) getListView().getEmptyView()).setText(getString(R.string.list_empty_updates));
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (UpdatableAppsActivity.needsUpdate) {
            setTitle(getString(PreferenceActivity.getBoolean(this, PreferenceActivity.PREFERENCE_UPDATES_ONLY)
                ? R.string.activity_title_updates_only
                : R.string.activity_title_updates_and_other_apps
            ));
            loadApps();
            setNeedsUpdate(false);
        }
        if (PreferenceActivity.getUpdateInterval(this) < 0) {
            Button checkUpdates = findViewById(R.id.check_updates);
            checkUpdates.setVisibility(View.VISIBLE);
            checkUpdates.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ForegroundUpdatableAppsTask task = getTask();
                    ForegroundUpdatableAppsTask clone = getTask();
                    task.setExplicitCheck(true);
                    clone.setExplicitCheck(true);
                    task.setTaskClone(clone);
                    task.execute();
                }
            });
        }
        updateAllReceiver = new UpdateAllReceiver(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != updateAllReceiver) {
            unregisterReceiver(updateAllReceiver);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
        setNeedsUpdate(true);
    }

    @Override
    protected void loadApps() {
        UpdatableAppsTask taskClone = getTask();
        UpdatableAppsTask task = getTask();
        task.setTaskClone(taskClone);
        task.execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (isGranted(requestCode, permissions, grantResults)) {
            launchUpdateAll();
        }
    }

    @Override
    protected ListItem getListItem(App app) {
        UpdatableAppBadge appBadge = new UpdatableAppBadge();
        appBadge.setApp(app);
        return appBadge;
    }

    private ForegroundUpdatableAppsTask getTask() {
        ForegroundUpdatableAppsTask task = new ForegroundUpdatableAppsTask(this);
        task.setExplicitCheck(PreferenceActivity.getUpdateInterval(this) != -1);
        task.setErrorView((TextView) getListView().getEmptyView());
        task.setContext(this);
        task.setProgressIndicator(findViewById(R.id.progress));
        return task;
    }

    public void addApps(List<App> apps, int labelStringId) {
        if (!apps.isEmpty()) {
            addSeparator(getString(labelStringId));
        }
        addApps(apps);
    }

    public void launchUpdateAll() {
        ((YalpStoreApplication) getApplicationContext()).setBackgroundUpdating(true);
        new UpdateChecker().onReceive(UpdatableAppsActivity.this, getIntent());
        Button button = findViewById(R.id.update_all);
        button.setEnabled(false);
        button.setText(R.string.list_updating);
    }
}

