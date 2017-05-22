package com.github.yeriomin.yalpstore.fragment.preference;

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
        Map<String, String> appNames = getInstalledAppNames();
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

        Preference.OnPreferenceChangeListener listener = new BlackListOnPreferenceChangeListener();
        blackOrWhite.setOnPreferenceChangeListener(listener);
        listener.onPreferenceChange(blackOrWhite, blackOrWhite.getValue());
    }

    private Map<String, String> getInstalledAppNames() {
        Map<String, String> appNames = new HashMap<>();
        for (App app: UpdatableAppsTask.getInstalledApps(activity)) {
            appNames.put(app.getPackageName(), app.getDisplayName());
        }
        return Util.sort(appNames);
    }

    private class BlackListOnPreferenceChangeListener implements Preference.OnPreferenceChangeListener {

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String value = (String) newValue;
            boolean isBlackList = null != value && value.equals(PreferenceActivity.LIST_BLACK);
            appList.setTitle(activity.getString(
                isBlackList
                    ? R.string.pref_update_list_black
                    : R.string.pref_update_list_white
            ));
            preference.setSummary(activity.getString(
                isBlackList
                    ? R.string.pref_update_list_white_or_black_black
                    : R.string.pref_update_list_white_or_black_white
            ));
            return true;
        }
    }
}
