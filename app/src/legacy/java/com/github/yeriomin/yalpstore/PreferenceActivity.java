package com.github.yeriomin.yalpstore;

import android.os.Bundle;

import com.github.yeriomin.yalpstore.fragment.preference.AllPreferences;

public class PreferenceActivity extends android.preference.PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        new ThemeManager().setTheme(this);
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        new AllPreferences(this).draw();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (!YalpStorePermissionManager.isGranted(requestCode, permissions, grantResults)) {
            finish();
        }
    }
}