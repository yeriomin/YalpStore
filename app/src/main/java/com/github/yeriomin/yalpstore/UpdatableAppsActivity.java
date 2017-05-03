package com.github.yeriomin.yalpstore;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.model.App;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            this.data.clear();
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
    protected Map<String, Object> formatApp(App app) {
        Map<String, Object> map = super.formatApp(app);
        String updated = app.getUpdated();
        if (!TextUtils.isEmpty(updated)) {
            map.put(LINE2, getString(R.string.list_line_2_updatable, updated));
        }
        String line3 = "";
        if (app.isSystem()) {
            line3 += getString(R.string.list_app_system) + " ";
        }
        if (app.isInPlayStore() && !showUpdatesOnly()) {
            line3 += getString(R.string.list_app_exists_in_play_store);
        }
        if (!TextUtils.isEmpty(line3)) {
            map.put(LINE3, line3);
        }
        map.put(ICON, app.getIconInfo());
        return map;
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

    private UpdatableAppsTask getTask() {
        UpdatableAppsTask task = new UpdatableAppsTask() {
            @Override
            protected void onPostExecute(Throwable e) {
                super.onPostExecute(e);
                clearApps();
                if (null != e && showUpdatesOnly()) {
                    return;
                }
                addApps(updatableApps, otherInstalledApps, explicitCheck);
                toggleUpdateAll(this.updatableApps.size() > 0);
                new CategoryManager(UpdatableAppsActivity.this).downloadCategoryNames();
            }
        };
        task.setErrorView((TextView) getListView().getEmptyView());
        task.setContext(this);
        task.setProgressIndicator(findViewById(R.id.progress));
        return task;
    }

    private void addApps(List<App> updatable, List<App> other, boolean explicitCheck) {
        if (showUpdatesOnly()) {
            addApps(updatable);
        } else {
            if (!updatable.isEmpty()) {
                data.add(getHeader(R.string.list_has_update));
                Collections.sort(updatable);
                addApps(updatable);
            }
            if (!other.isEmpty()) {
                if (!(PreferenceActivity.getUpdateInterval(this) < 0) || explicitCheck) {
                    data.add(getHeader(R.string.list_no_update));
                }
                Collections.sort(other);
                addApps(other);
            }
        }
    }

    private boolean showUpdatesOnly() {
        return PreferenceActivity.getBoolean(this, PreferenceActivity.PREFERENCE_UPDATES_ONLY);
    }

    private Map<String, Object> getHeader(int headerTextResId) {
        Map<String, Object> map = new HashMap<>();
        map.put(LINE1, getString(headerTextResId));
        return map;
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

