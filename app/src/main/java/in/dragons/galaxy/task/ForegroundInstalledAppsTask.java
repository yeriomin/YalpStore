package in.dragons.galaxy.task;

import in.dragons.galaxy.InstalledAppsActivity;
import in.dragons.galaxy.R;
import in.dragons.galaxy.fragment.FilterMenu;
import in.dragons.galaxy.model.App;

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
        setIncludeSystemApps(new FilterMenu(activity).getFilterPreferences().isSystemApps());
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
