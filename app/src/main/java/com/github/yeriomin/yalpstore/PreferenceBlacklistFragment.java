package com.github.yeriomin.yalpstore;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.ListPreference;
import android.preference.Preference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreferenceBlacklistFragment extends PreferenceFragment {

    private ListPreference blackOrWhite;
    private MultiSelectListPreference appList;

    public PreferenceBlacklistFragment(PreferenceActivity activity) {
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
        PackageManager pm = activity.getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(PackageManager.GET_META_DATA);
        Map<String, String> appNames = new HashMap<>();
        for (PackageInfo info: packages) {
            if ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                // This is a system app - skipping
                continue;
            }
            appNames.put(info.packageName, pm.getApplicationLabel(info.applicationInfo).toString());
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
