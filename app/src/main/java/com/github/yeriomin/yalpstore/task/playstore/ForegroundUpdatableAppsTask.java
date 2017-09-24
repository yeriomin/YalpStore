package com.github.yeriomin.yalpstore.task.playstore;

import android.view.View;
import android.widget.Button;

import com.github.yeriomin.playstoreapi.GooglePlayAPI;
import com.github.yeriomin.yalpstore.BlackWhiteListManager;
import com.github.yeriomin.yalpstore.BuildConfig;
import com.github.yeriomin.yalpstore.CategoryManager;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.UpdatableAppsActivity;
import com.github.yeriomin.yalpstore.YalpStoreApplication;
import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.selfupdate.UpdaterFactory;
import com.github.yeriomin.yalpstore.task.InstalledAppsTask;

import java.io.IOException;
import java.util.List;

public class ForegroundUpdatableAppsTask extends UpdatableAppsTask implements CloneableTask {

    private UpdatableAppsActivity activity;

    public ForegroundUpdatableAppsTask(UpdatableAppsActivity activity) {
        this.activity = activity;
        setContext(activity);
    }

    @Override
    public CloneableTask clone() {
        ForegroundUpdatableAppsTask task = new ForegroundUpdatableAppsTask(this.activity);
        task.setErrorView(errorView);
        task.setProgressIndicator(progressIndicator);
        return task;
    }

    @Override
    protected List<App> getResult(GooglePlayAPI api, String... packageNames) throws IOException {
        super.getResult(api, packageNames);
        if (!success() || !new BlackWhiteListManager(context).isUpdatable(BuildConfig.APPLICATION_ID)) {
            return updatableApps;
        }
        int latestVersionCode = UpdaterFactory.get(context).getLatestVersionCode();
        if (latestVersionCode > BuildConfig.VERSION_CODE) {
            App yalp = InstalledAppsTask.getInstalledApp(context.getPackageManager(), BuildConfig.APPLICATION_ID);
            if (null == yalp) {
                return updatableApps;
            }
            yalp.setVersionCode(latestVersionCode);
            yalp.setVersionName("0." + latestVersionCode);
            updatableApps.add(yalp);
        }
        return updatableApps;
    }

    @Override
    protected void onPostExecute(List<App> result) {
        super.onPostExecute(result);
        activity.clearApps();
        if (!success()) {
            return;
        }
        activity.addApps(updatableApps);
        toggleUpdateAll(this.updatableApps.size() > 0);
        new CategoryManager(activity).downloadCategoryNames();
    }

    private void toggleUpdateAll(boolean enable) {
        Button button = activity.findViewById(R.id.main_button);
        button.setText(R.string.list_update_all);
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
}
