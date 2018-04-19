package com.dragons.aurora.fragment.preference;

import android.preference.ListPreference;
import android.preference.Preference;

import com.dragons.aurora.fragment.PreferenceFragment;

public class InstallationMethod extends Abstract {

    private ListPreference installationMethod;

    public InstallationMethod(PreferenceFragment activity) {
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
