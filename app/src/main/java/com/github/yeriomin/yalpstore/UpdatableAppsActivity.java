package com.github.yeriomin.yalpstore;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.view.ListItem;
import com.github.yeriomin.yalpstore.view.UpdatableAppBadge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UpdatableAppsActivity extends AppListActivity {

    static private final int PERMISSIONS_REQUEST_CODE = 91;

    static private boolean needsUpdate;

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
            setTitle(getString(showUpdatesOnly()
                ? R.string.activity_title_updates_only
                : R.string.activity_title_updates_and_other_apps
            ));
            loadApps();
            setNeedsUpdate(false);
        }
        if (PreferenceActivity.getUpdateInterval(this) < 0) {
            Button checkUpdates = (Button) findViewById(R.id.check_updates);
            checkUpdates.setVisibility(View.VISIBLE);
            checkUpdates.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UpdatableAppsTask task = getTask();
                    task.setExplicitCheck(true);
                    task.setTaskClone(getTask());
                    task.execute();
                }
            });
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
        if (requestCode == PERMISSIONS_REQUEST_CODE
            && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            new UpdateChecker().onReceive(this, getIntent());
        }
    }

    @Override
    protected ListItem getListItem(App app) {
        UpdatableAppBadge appBadge = new UpdatableAppBadge();
        appBadge.setApp(app);
        return appBadge;
    }

    private UpdatableAppsTask getTask() {
        UpdatableAppsTask task = new UpdatableAppsTask() {
            @Override
            protected void onPostExecute(Throwable e) {
                super.onPostExecute(e);
                clearApps();
                if (null != e && showUpdatesOnly()) {
                    return;
                }
                List<App> otherInstalledApps = new ArrayList<>(this.installedApps.values());
                Collections.sort(otherInstalledApps);
                if (showUpdatesOnly()) {
                    addApps(updatableApps);
                } else if (null != e || !explicitCheck) {
                    addApps(otherInstalledApps);
                } else {
                    addApps(updatableApps, R.string.list_has_update);
                    addApps(otherInstalledApps, R.string.list_no_update);
                }
                toggleUpdateAll(this.updatableApps.size() > 0);
                new CategoryManager(UpdatableAppsActivity.this).downloadCategoryNames();
            }
        };
        task.setExplicitCheck(PreferenceActivity.getUpdateInterval(this) != -1);
        task.setErrorView((TextView) getListView().getEmptyView());
        task.setContext(this);
        task.setProgressIndicator(findViewById(R.id.progress));
        return task;
    }

    private void addApps(List<App> apps, int labelStringId) {
        if (!apps.isEmpty()) {
            addSeparator(getString(labelStringId));
        }
        addApps(apps);
    }

    private boolean showUpdatesOnly() {
        return PreferenceActivity.getBoolean(this, PreferenceActivity.PREFERENCE_UPDATES_ONLY);
    }

    private void toggleUpdateAll(boolean enable) {
        Button button = (Button) findViewById(R.id.update_all);
        button.setVisibility(enable ? View.VISIBLE : View.GONE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission()) {
                    new UpdateChecker().onReceive(UpdatableAppsActivity.this, getIntent());
                } else {
                    requestPermission();
                }
            }
        });
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                PERMISSIONS_REQUEST_CODE
            );
        }
    }
}

