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
                    UpdatableAppsTask clone = getTask();
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
        if (requestCode == PERMISSIONS_REQUEST_CODE
            && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            launchUpdateAll();
        }
    }

    @Override
    protected ListItem getListItem(App app) {
        UpdatableAppBadge appBadge = new UpdatableAppBadge();
        appBadge.setApp(app);
        return appBadge;
    }

    private UpdatableAppsTask getTask() {
        UpdatableAppsTask task = new LocalUpdatableAppsTask(this);
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
        Button button = findViewById(R.id.update_all);
        button.setVisibility(enable ? View.VISIBLE : View.GONE);
        if (((YalpStoreApplication) getApplication()).isBackgroundUpdating()) {
            button.setEnabled(false);
            button.setText(R.string.list_updating);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission()) {
                    launchUpdateAll();
                } else {
                    requestPermission();
                }
            }
        });
    }

    private void launchUpdateAll() {
        ((YalpStoreApplication) getApplicationContext()).setBackgroundUpdating(true);
        new UpdateChecker().onReceive(UpdatableAppsActivity.this, getIntent());
        Button button = findViewById(R.id.update_all);
        button.setEnabled(false);
        button.setText(R.string.list_updating);
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

    static class LocalUpdatableAppsTask extends UpdatableAppsTask {

        private UpdatableAppsActivity activity;

        public LocalUpdatableAppsTask(UpdatableAppsActivity activity) {
            this.activity = activity;
        }

        @Override
        protected Throwable doInBackground(String... params) {
            Throwable result = super.doInBackground(params);
            if (null != result || !explicitCheck) {
                return result;
            }
            int latestVersionCode = SelfUpdateChecker.getLatestVersionCode();
            if (latestVersionCode > BuildConfig.VERSION_CODE) {
                App yalp = installedApps.get(BuildConfig.APPLICATION_ID);
                installedApps.remove(yalp);
                yalp.setVersionCode(latestVersionCode);
                yalp.setVersionName("0." + latestVersionCode);
                updatableApps.add(yalp);
            }
            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Button button = activity.findViewById(R.id.check_updates);
            button.setEnabled(false);
            button.setText(R.string.details_download_checking);
        }

        @Override
        protected void onPostExecute(Throwable e) {
            super.onPostExecute(e);
            activity.clearApps();
            if (null != e && activity.showUpdatesOnly()) {
                return;
            }
            List<App> otherInstalledApps = new ArrayList<>(this.installedApps.values());
            Collections.sort(otherInstalledApps);
            if (activity.showUpdatesOnly()) {
                activity.addApps(updatableApps);
            } else if (null != e || !explicitCheck) {
                activity.addApps(otherInstalledApps);
            } else {
                activity.addApps(updatableApps, R.string.list_has_update);
                activity.addApps(otherInstalledApps, R.string.list_no_update);
            }
            activity.toggleUpdateAll(this.updatableApps.size() > 0);
            new CategoryManager(activity).downloadCategoryNames();
            Button button = activity.findViewById(R.id.check_updates);
            button.setEnabled(true);
            button.setText(R.string.list_check_updates);
        }
    }
}

