package com.github.yeriomin.yalpstore.task;

import com.github.yeriomin.yalpstore.InstalledAppsActivity;
import com.github.yeriomin.yalpstore.PreferenceActivity;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.model.App;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ForegroundInstalledAppsTask extends InstalledAppsTask {

    private InstalledAppsActivity activity;

    public ForegroundInstalledAppsTask(InstalledAppsActivity activity) {
        this.activity = activity;
        setContext(activity.getApplicationContext());
        setProgressIndicator(activity.findViewById(R.id.progress));
        setIncludeSystemApps(PreferenceActivity.getBoolean(context, PreferenceActivity.PREFERENCE_SHOW_SYSTEM_APPS));
    }

    @Override
    protected void onPostExecute(Map<String, App> result) {
        super.onPostExecute(result);
        activity.clearApps();
        List<App> installedApps = new ArrayList<>(result.values());
        Collections.sort(installedApps);
        activity.addApps(installedApps);
    }
}
