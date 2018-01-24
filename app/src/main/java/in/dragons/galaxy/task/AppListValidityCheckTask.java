package in.dragons.galaxy.task;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;

import in.dragons.galaxy.AppListActivity;
import in.dragons.galaxy.BlackWhiteListManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AppListValidityCheckTask extends AsyncTask<String, Void, Set<String>> {

    private AppListActivity activity;
    protected boolean includeSystemApps = false;
    protected boolean respectUpdateBlacklist = false;

    public void setIncludeSystemApps(boolean includeSystemApps) {
        this.includeSystemApps = includeSystemApps;
    }

    public void setRespectUpdateBlacklist(boolean respectUpdateBlacklist) {
        this.respectUpdateBlacklist = respectUpdateBlacklist;
    }

    public AppListValidityCheckTask(AppListActivity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPostExecute(Set<String> installedPackageNames) {
        super.onPostExecute(installedPackageNames);
        Set<String> newPackageNames = new HashSet<>(installedPackageNames);
        newPackageNames.removeAll(activity.getListedPackageNames());
        if (!respectUpdateBlacklist && newPackageNames.size() > 0) {
            activity.loadApps();
            return;
        }
        Set<String> removedPackageNames = new HashSet<>(activity.getListedPackageNames());
        removedPackageNames.removeAll(installedPackageNames);
        for (String packageName: removedPackageNames) {
            activity.removeApp(packageName);
        }
    }

    @Override
    protected Set<String> doInBackground(String... strings) {
        Set<String> installedApps = new HashSet<>();
        List<PackageInfo> installedPackages = new ArrayList<>();
        try {
            installedPackages.addAll(activity.getPackageManager().getInstalledPackages(0));
        } catch (RuntimeException e) {
            // Sometimes TransactionTooLargeException is thrown even though getInstalledPackages is
            // called with 0 flags. App list validity check is not essential, so this can be ignored
            // TODO: There might be a way to avoid this exception, although I doubt it
        }
        BlackWhiteListManager manager = new BlackWhiteListManager(activity);
        for (PackageInfo reducedPackageInfo: installedPackages) {
            if (!includeSystemApps
                && null != reducedPackageInfo.applicationInfo
                && (reducedPackageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0
            ) {
                continue;
            }
            if (respectUpdateBlacklist && !manager.isUpdatable(reducedPackageInfo.packageName)) {
                continue;
            }
            installedApps.add(reducedPackageInfo.packageName);
        }
        return installedApps;
    }
}
