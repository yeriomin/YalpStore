package com.github.yeriomin.yalpstore.fragment.preference;

import android.preference.ListPreference;
import android.preference.Preference;

import com.github.yeriomin.yalpstore.MultiSelectListPreference;
import com.github.yeriomin.yalpstore.PreferenceActivity;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.Util;
import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.task.InstalledAppsTask;

import java.util.HashMap;
import java.util.Map;

public class Blacklist extends Abstract {

    private ListPreference blackOrWhite;
    private MultiSelectListPreference appList;

    public Blacklist(PreferenceActivity activity) {
        super(activity);
    }

    public void setBlackOrWhite(ListPreference blackOrWhite) {
        this.blackOrWhite = blackOrWhite;
    }

    public void setAppList(MultiSelectListPreference appList) {
        this.appList = appList;
    }

    @Override
    public void draw() {
        AppListTask task = new AppListTask(appList);
        task.setIncludeSystemApps(true);
        task.execute();

        Preference.OnPreferenceChangeListener listener = new BlackListOnPreferenceChangeListener(appList);
        blackOrWhite.setOnPreferenceChangeListener(listener);
        listener.onPreferenceChange(blackOrWhite, blackOrWhite.getValue());
    }

    static class BlackListOnPreferenceChangeListener implements Preference.OnPreferenceChangeListener {

        private MultiSelectListPreference appList;

        public BlackListOnPreferenceChangeListener(MultiSelectListPreference appList) {
            this.appList = appList;
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String value = (String) newValue;
            boolean isBlackList = null != value && value.equals(PreferenceActivity.LIST_BLACK);
            appList.setTitle(appList.getContext().getString(
                isBlackList
                    ? R.string.pref_update_list_black
                    : R.string.pref_update_list_white
            ));
            appList.setDialogTitle(appList.getTitle());
            preference.setSummary(appList.getContext().getString(
                isBlackList
                    ? R.string.pref_update_list_white_or_black_black
                    : R.string.pref_update_list_white_or_black_white
            ));
            return true;
        }
    }

    static class AppListTask extends InstalledAppsTask {

        private MultiSelectListPreference appList;
        private Map<String, String> appNames;

        public AppListTask(MultiSelectListPreference appList) {
            this.appList = appList;
            setContext(appList.getContext());
        }

        @Override
        protected void onPreExecute() {
            appList.setEntries(new String[0]);
            appList.setEntryValues(new String[0]);
        }

        @Override
        protected void onPostExecute(Map<String, App> installedApps) {
            int count = appNames.size();
            appList.setEntries(appNames.values().toArray(new String[count]));
            appList.setEntryValues(appNames.keySet().toArray(new String[count]));
        }

        @Override
        protected Map<String, App> doInBackground(String... strings) {
            Map<String, App> installedApps = super.doInBackground(strings);
            Map<String, String> appNames = new HashMap<>();
            for (String packageName: installedApps.keySet()) {
                appNames.put(packageName, installedApps.get(packageName).getDisplayName());
            }
            this.appNames = Util.sort(appNames);
            return installedApps;
        }
    }
}
