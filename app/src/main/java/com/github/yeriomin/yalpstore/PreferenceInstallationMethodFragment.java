package com.github.yeriomin.yalpstore;

import android.preference.ListPreference;
import android.preference.Preference;

public class PreferenceInstallationMethodFragment extends PreferenceFragment {

    private ListPreference installationMethod;

    public PreferenceInstallationMethodFragment(PreferenceActivity activity) {
        super(activity);
    }

    public void setInstallationMethodPreference(ListPreference installationMethod) {
        this.installationMethod = installationMethod;
    }

    @Override
    public void draw() {
        Preference.OnPreferenceChangeListener listener = new OnInstallationMethodChangeListener(activity);
        listener.onPreferenceChange(installationMethod, installationMethod.getValue());
        installationMethod.setOnPreferenceChangeListener(listener);
    }
}
