package com.dragons.aurora.fragment;

import com.dragons.aurora.task.AppListValidityCheckTask;

abstract public class AppListFragment extends UtilFragment {
    protected void checkAppListValidity() {
        AppListValidityCheckTask task = new AppListValidityCheckTask(this);
        task.setRespectUpdateBlacklist(true);
        task.setIncludeSystemApps(true);
        task.execute();
    }
}
