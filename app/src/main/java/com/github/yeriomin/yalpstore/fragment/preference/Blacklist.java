package com.github.yeriomin.yalpstore.fragment.preference;

import android.os.AsyncTask;
import android.preference.ListPreference;
import android.preference.Preference;

import com.github.yeriomin.yalpstore.MultiSelectListPreference;
import com.github.yeriomin.yalpstore.PreferenceActivity;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.UpdatableAppsActivity;
import com.github.yeriomin.yalpstore.UpdatableAppsTask;
import com.github.yeriomin.yalpstore.Util;
import com.github.yeriomin.yalpstore.model.App;

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
        new AppListTask(appList).execute();

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
            preference.setSummary(appList.getContext().getString(
                isBlackList
                    ? R.string.pref_update_list_white_or_black_black
                    : R.string.pref_update_list_white_or_black_white
            ));
            return true;
        }
    }

    static class AppListTask extends AsyncTask<Void, Void, Void> {

        private MultiSelectListPreference appList;
        private Map<String, String> appNames;

        public AppListTask(MultiSelectListPreference appList) {
            this.appList = appList;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            int count = appNames.size();
            appList.setEntries(appNames.values().toArray(new String[count]));
            appList.setEntryValues(appNames.keySet().toArray(new String[count]));
            appList.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    UpdatableAppsActivity.setNeedsUpdate(true);
                    return true;
                }
            });
        }

        @Override
        protected Void doInBackground(Void... voids) {
            appNames = getInstalledAppNames();
            return null;
        }

        private Map<String, String> getInstalledAppNames() {
            Map<String, String> appNames = new HashMap<>();
            Map<String, App> installedApps = UpdatableAppsTask.getInstalledApps(appList.getContext());
            if (!PreferenceActivity.getBoolean(appList.getContext(), PreferenceActivity.PREFERENCE_SHOW_SYSTEM_APPS)) {
                installedApps = UpdatableAppsTask.filterSystemApps(installedApps);
            }
            for (String packageName: installedApps.keySet()) {
                appNames.put(packageName, installedApps.get(packageName).getDisplayName());
            }
            return Util.sort(appNames);
        }
    }
}
