package com.github.yeriomin.yalpstore;

import android.app.Fragment;
import android.os.Bundle;
import android.preference.Preference;
import android.support.annotation.Nullable;

import com.github.yeriomin.yalpstore.fragment.preference.PreferenceFragment;

public class PreferenceActivity extends YalpStoreActivity {

    private PreferenceFragment fragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences_layout);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof PreferenceFragment) {
            this.fragment = (PreferenceFragment) fragment;
        }
    }

    public Preference findPreference(CharSequence key) {
        if (fragment == null) {
            return null;
        }
        return fragment.findPreference(key);
    }
}
