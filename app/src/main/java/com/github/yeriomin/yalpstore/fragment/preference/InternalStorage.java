package com.github.yeriomin.yalpstore.fragment.preference;

import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;

import com.github.yeriomin.yalpstore.Paths;
import com.github.yeriomin.yalpstore.PreferenceActivity;
import com.github.yeriomin.yalpstore.PreferenceUtil;

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
                    activity.findPreference(PreferenceUtil.PREFERENCE_DOWNLOAD_DIRECTORY).setSummary(activity.getFilesDir().getAbsolutePath());
                    ((CheckBoxPreference) activity.findPreference(PreferenceUtil.PREFERENCE_AUTO_INSTALL)).setChecked(true);
                    ((CheckBoxPreference) activity.findPreference(PreferenceUtil.PREFERENCE_DELETE_APK_AFTER_INSTALL)).setChecked(true);
                } else {
                    activity.findPreference(PreferenceUtil.PREFERENCE_DOWNLOAD_DIRECTORY).setSummary(new File(
                        Paths.getStorageRoot(activity),
                        PreferenceManager.getDefaultSharedPreferences(activity).getString(PreferenceUtil.PREFERENCE_DOWNLOAD_DIRECTORY, "")
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
