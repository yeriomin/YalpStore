package com.github.yeriomin.yalpstore.task.playstore;

import com.github.yeriomin.playstoreapi.GooglePlayAPI;
import com.github.yeriomin.yalpstore.BlackWhiteListManager;
import com.github.yeriomin.yalpstore.BuildConfig;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.UpdatableAppsActivity;
import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.selfupdate.UpdaterFactory;
import com.github.yeriomin.yalpstore.task.InstalledAppsTask;
import com.github.yeriomin.yalpstore.view.UpdatableAppsButtonAdapter;

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
        if (!new BlackWhiteListManager(context).isUpdatable(BuildConfig.APPLICATION_ID)) {
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
    protected void onPreExecute() {
        super.onPreExecute();
        this.errorView.setText("");
    }

    @Override
    protected void onPostExecute(List<App> result) {
        super.onPostExecute(result);
        activity.clearApps();
        activity.addApps(updatableApps);
        if (success() && updatableApps.isEmpty()) {
            this.errorView.setText(R.string.list_empty_updates);
        }
        UpdatableAppsButtonAdapter buttonAdapter = new UpdatableAppsButtonAdapter(activity.findViewById(R.id.main_button));
        buttonAdapter.init(activity);
        if (updatableApps.isEmpty()) {
            buttonAdapter.hide();
        } else {
            buttonAdapter.show();
        }
    }
}
