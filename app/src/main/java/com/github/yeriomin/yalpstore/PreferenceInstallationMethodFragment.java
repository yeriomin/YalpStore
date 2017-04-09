package com.github.yeriomin.yalpstore;

import android.Manifest;
import android.content.pm.PackageManager;
import android.preference.ListPreference;
import android.preference.Preference;
import android.widget.Toast;

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
        Preference.OnPreferenceChangeListener listener = new OnInstallationMethodChangeListener();
        listener.onPreferenceChange(installationMethod, installationMethod.getValue());
        installationMethod.setOnPreferenceChangeListener(listener);
    }

    private int getInstallationMethodSummaryStringId(String installationMethod) {
        if (null == installationMethod) {
            return R.string.pref_installation_method_default;
        }
        int summaryId;
        switch (installationMethod) {
            case PreferenceActivity.INSTALLATION_METHOD_PRIVILEGED:
                summaryId = R.string.pref_installation_method_privileged;
                break;
            case PreferenceActivity.INSTALLATION_METHOD_ROOT:
                summaryId = R.string.pref_installation_method_root;
                break;
            default:
                summaryId = R.string.pref_installation_method_default;
                break;
        }
        return summaryId;
    }

    class OnInstallationMethodChangeListener implements Preference.OnPreferenceChangeListener {

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String oldValue = ((ListPreference) preference).getValue();
            if (null != oldValue && !oldValue.equals(newValue)) {
                if (PreferenceActivity.INSTALLATION_METHOD_PRIVILEGED.equals(newValue)) {
                    return checkPrivileged();
                } else if (PreferenceActivity.INSTALLATION_METHOD_ROOT.equals(newValue)) {
                    new CheckSuTask(activity).execute();
                }
            }
            preference.setSummary(activity.getString(getInstallationMethodSummaryStringId((String) newValue)));
            return true;
        }

        private boolean checkPrivileged() {
            PackageManager pm = activity.getPackageManager();
            boolean privileged = pm.checkPermission(Manifest.permission.INSTALL_PACKAGES, BuildConfig.APPLICATION_ID) == PackageManager.PERMISSION_GRANTED
                && pm.checkPermission(Manifest.permission.DELETE_PACKAGES, BuildConfig.APPLICATION_ID) == PackageManager.PERMISSION_GRANTED
            ;
            if (!privileged) {
                Toast.makeText(activity, R.string.pref_not_privileged, Toast.LENGTH_LONG).show();
            }
            return privileged;
        }
    }
}
