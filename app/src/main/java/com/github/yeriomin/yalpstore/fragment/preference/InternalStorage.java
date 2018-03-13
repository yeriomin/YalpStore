package com.github.yeriomin.yalpstore.fragment.preference;

import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;

import com.github.yeriomin.yalpstore.Paths;
import com.github.yeriomin.yalpstore.PreferenceActivity;

import java.io.File;

public class InternalStorage extends Abstract {

    private CheckBoxPreference preference;

    public InternalStorage setPreference(CheckBoxPreference preference) {
        this.preference = preference;
        return this;
    }

    @Override
    public void draw() {
        preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if ((Boolean) newValue) {
                    activity.findPreference(PreferenceActivity.PREFERENCE_DOWNLOAD_DIRECTORY).setSummary(activity.getFilesDir().getAbsolutePath());
                    ((CheckBoxPreference) activity.findPreference(PreferenceActivity.PREFERENCE_AUTO_INSTALL)).setChecked(true);
                    ((CheckBoxPreference) activity.findPreference(PreferenceActivity.PREFERENCE_DELETE_APK_AFTER_INSTALL)).setChecked(true);
                } else {
                    activity.findPreference(PreferenceActivity.PREFERENCE_DOWNLOAD_DIRECTORY).setSummary(new File(
                        Paths.getStorageRoot(activity),
                        PreferenceManager.getDefaultSharedPreferences(activity).getString(PreferenceActivity.PREFERENCE_DOWNLOAD_DIRECTORY, "")
                    ).getAbsolutePath());
                }
                return true;
            }
        });
    }

    public InternalStorage(PreferenceActivity activity) {
        super(activity);
    }
}
