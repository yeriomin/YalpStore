package com.github.yeriomin.yalpstore.fragment.preference;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.preference.EditTextPreference;
import android.preference.Preference;

import com.github.yeriomin.yalpstore.ContextUtil;
import com.github.yeriomin.yalpstore.PreferenceActivity;
import com.github.yeriomin.yalpstore.R;

import java.io.File;
import java.io.IOException;

public class DownloadDirectory extends Abstract {

    private EditTextPreference preference;

    public DownloadDirectory setPreference(EditTextPreference preference) {
        this.preference = preference;
        return this;
    }

    @Override
    public void draw() {
        preference.setSummary(preference.getText());
        preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                String newValue = (String) o;
                boolean result = checkNewValue(newValue);
                if (!result) {
                    ContextUtil.toast(activity, R.string.error_downloads_directory_not_writable);
                } else {
                    preference.setSummary(newValue);
                }
                return result;
            }

            private boolean checkNewValue(String newValue) {
                try {
                    File newDir = new File(Environment.getExternalStorageDirectory(), newValue).getCanonicalFile();
                    if (newDir.exists()) {
                        return true;
                    }
                    if (activity.checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        return newDir.mkdirs();
                    }
                    return true;
                } catch (IOException e) {
                    return false;
                }
            }
        });
    }

    public DownloadDirectory(PreferenceActivity activity) {
        super(activity);
    }
}
