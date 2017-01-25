package com.github.yeriomin.yalpstore;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;

import java.util.ArrayList;
import java.util.List;

public class PreferenceActivity extends android.preference.PreferenceActivity {

    public static final String PREFERENCE_EMAIL = "PREFERENCE_EMAIL";
    public static final String PREFERENCE_AUTH_TOKEN = "PREFERENCE_AUTH_TOKEN";
    public static final String PREFERENCE_GSF_ID = "PREFERENCE_GSF_ID";
    public static final String PREFERENCE_AUTO_INSTALL = "PREFERENCE_AUTO_INSTALL";
    public static final String PREFERENCE_HIDE_NONFREE_APPS = "PREFERENCE_HIDE_NONFREE_APPS";
    public static final String PREFERENCE_UPDATE_LIST_WHITE_OR_BLACK = "PREFERENCE_UPDATE_LIST_WHITE_OR_BLACK";
    public static final String PREFERENCE_UPDATE_LIST = "PREFERENCE_UPDATE_LIST";

    public static final String LIST_WHITE = "white";
    public static final String LIST_BLACK = "black";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        PackageManager pm = getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(PackageManager.GET_META_DATA);
        List<CharSequence> entries = new ArrayList<>();
        List<CharSequence> entryValues = new ArrayList<>();
        for (PackageInfo packageInfo: packages) {
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                // This is a system app - skipping
                continue;
            }
            entries.add(pm.getApplicationLabel(packageInfo.applicationInfo).toString());
            entryValues.add(packageInfo.packageName);
        }

        final MultiSelectListPreference m = (MultiSelectListPreference) findPreference(PREFERENCE_UPDATE_LIST);
        ListPreference blackOrWhite = (ListPreference) findPreference(PREFERENCE_UPDATE_LIST_WHITE_OR_BLACK);
        m.setTitle(blackOrWhite.getValue() == LIST_BLACK
            ? getString(R.string.pref_update_list_black)
            : getString(R.string.pref_update_list_white)
        );
        m.setEntries(entries.toArray(new CharSequence[entries.size()]));
        m.setEntryValues(entryValues.toArray(new CharSequence[entryValues.size()]));

        blackOrWhite.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String value = (String) newValue;
                switch (value) {
                    case LIST_BLACK:
                        m.setTitle(getString(R.string.pref_update_list_black));
                        break;
                    case LIST_WHITE:
                        m.setTitle(getString(R.string.pref_update_list_white));
                        break;
                }
                return true;
            }
        });
    }
}