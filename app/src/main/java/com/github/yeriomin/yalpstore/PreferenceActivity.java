package com.github.yeriomin.yalpstore;

import android.os.Bundle;
import android.preference.Preference;
import android.widget.Toast;

public class PreferenceActivity extends android.preference.PreferenceActivity {

    public static final String PREFERENCE_EMAIL = "PREFERENCE_EMAIL";
    public static final String PREFERENCE_AUTH_TOKEN = "PREFERENCE_AUTH_TOKEN";
    public static final String PREFERENCE_GSF_ID = "PREFERENCE_GSF_ID";
    public static final String PREFERENCE_AUTO_INSTALL = "PREFERENCE_AUTO_INSTALL";
    public static final String PREFERENCE_HIDE_NONFREE_APPS = "PREFERENCE_HIDE_NONFREE_APPS";
    public static final String PREFERENCE_IGNORED_APPS = "PREFERENCE_IGNORED_APPS";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        Preference button = findPreference(PREFERENCE_IGNORED_APPS);
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                IgnoredAppsManager manager = new IgnoredAppsManager(getApplicationContext());
                manager.clear();
                Toast.makeText(getApplicationContext(), R.string.pref_done, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }
}