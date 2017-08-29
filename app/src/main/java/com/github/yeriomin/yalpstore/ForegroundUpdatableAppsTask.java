package com.github.yeriomin.yalpstore;

import android.view.View;
import android.widget.Button;

import com.github.yeriomin.yalpstore.model.App;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class ForegroundUpdatableAppsTask extends UpdatableAppsTask {

    private UpdatableAppsActivity activity;

    public ForegroundUpdatableAppsTask(UpdatableAppsActivity activity) {
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
            App yalp = getSelf();
            installedApps.remove(BuildConfig.APPLICATION_ID);
            if (null == yalp) {
                return null;
            }
            yalp.setVersionCode(latestVersionCode);
            yalp.setVersionName("0." + latestVersionCode);
            updatableApps.add(yalp);
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Button button = activity.findViewById(R.id.check_updates);
        button.setEnabled(false);
        button.setText(R.string.ellipsis);
    }

    @Override
    protected void onPostExecute(Throwable e) {
        super.onPostExecute(e);
        activity.clearApps();
        if (null != e && showUpdatesOnly()) {
            return;
        }
        List<App> otherInstalledApps = new ArrayList<>(this.installedApps.values());
        Collections.sort(otherInstalledApps);
        if (showUpdatesOnly()) {
            activity.addApps(updatableApps);
        } else if (null != e || !explicitCheck) {
            activity.addApps(otherInstalledApps);
        } else {
            activity.addApps(updatableApps, R.string.list_has_update);
            activity.addApps(otherInstalledApps, R.string.list_no_update);
        }
        toggleUpdateAll(this.updatableApps.size() > 0);
        new CategoryManager(activity).downloadCategoryNames();
        Button button = activity.findViewById(R.id.check_updates);
        button.setEnabled(true);
        button.setText(R.string.list_check_updates);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        Button button = activity.findViewById(R.id.check_updates);
        button.setEnabled(false);
        button.setText(R.string.details_download_checking);
    }

    private App getSelf() {
        if (installedApps.containsKey(BuildConfig.APPLICATION_ID)) {
            return installedApps.get(BuildConfig.APPLICATION_ID);
        } else {
            return getInstalledApp(context.getPackageManager(), BuildConfig.APPLICATION_ID);
        }
    }

    private void toggleUpdateAll(boolean enable) {
        Button button = activity.findViewById(R.id.update_all);
        button.setVisibility(enable ? View.VISIBLE : View.GONE);
        if (((YalpStoreApplication) activity.getApplication()).isBackgroundUpdating()) {
            button.setEnabled(false);
            button.setText(R.string.list_updating);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activity.checkPermission()) {
                    activity.launchUpdateAll();
                } else {
                    activity.requestPermission();
                }
            }
        });
    }

    private boolean showUpdatesOnly() {
        return PreferenceActivity.getBoolean(context, PreferenceActivity.PREFERENCE_UPDATES_ONLY);
    }
}
