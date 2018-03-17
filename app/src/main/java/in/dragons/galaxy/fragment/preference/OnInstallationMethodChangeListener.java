package in.dragons.galaxy.fragment.preference;

import android.Manifest;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.ListPreference;
import android.preference.Preference;
import android.widget.Toast;

import in.dragons.galaxy.BuildConfig;
import in.dragons.galaxy.fragment.PreferenceFragment;
import in.dragons.galaxy.R;
import in.dragons.galaxy.model.App;
import in.dragons.galaxy.task.CheckShellTask;
import in.dragons.galaxy.task.CheckSuTask;
import in.dragons.galaxy.task.ConvertToSystemTask;

class OnInstallationMethodChangeListener implements Preference.OnPreferenceChangeListener {

    private PreferenceFragment activity;

    public OnInstallationMethodChangeListener(PreferenceFragment activity) {
        this.activity = activity;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String oldValue = ((ListPreference) preference).getValue();
        if (null != oldValue && !oldValue.equals(newValue)) {
            if (PreferenceFragment.INSTALLATION_METHOD_PRIVILEGED.equals(newValue)) {
                if (!checkPrivileged()) {
                    return false;
                }
            } else if (PreferenceFragment.INSTALLATION_METHOD_ROOT.equals(newValue)) {
                new CheckSuTask(activity).execute();
            }
        }
        preference.setSummary(activity.getString(getInstallationMethodSummaryStringId((String) newValue)));
        return true;
    }

    private int getInstallationMethodSummaryStringId(String installationMethod) {
        if (null == installationMethod) {
            return R.string.pref_installation_method_default;
        }
        int summaryId;
        switch (installationMethod) {
            case PreferenceFragment.INSTALLATION_METHOD_PRIVILEGED:
                summaryId = R.string.pref_installation_method_privileged;
                break;
            case PreferenceFragment.INSTALLATION_METHOD_ROOT:
                summaryId = R.string.pref_installation_method_root;
                break;
            default:
                summaryId = R.string.pref_installation_method_default;
                break;
        }
        return summaryId;
    }

    private boolean checkPrivileged() {
        boolean privileged = activity.getActivity().getPackageManager().checkPermission(Manifest.permission.INSTALL_PACKAGES, BuildConfig.APPLICATION_ID) == PackageManager.PERMISSION_GRANTED;
        if (!privileged) {
            new LocalCheckSuTask(activity).execute();
        }
        return privileged;
    }

    static class LocalCheckSuTask extends CheckSuTask {

        public LocalCheckSuTask(PreferenceFragment activity) {
            super(activity);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (!available) {
                Toast.makeText(activity.getActivity().getApplicationContext(), R.string.pref_not_privileged, Toast.LENGTH_LONG).show();
                return;
            }
            showPrivilegedInstallationDialog();
        }

        private void showPrivilegedInstallationDialog() {
            CheckShellTask checkShellTask = new CheckShellTask(activity.getActivity());
            checkShellTask.setPrimaryTask(new ConvertToSystemTask(activity.getActivity(), getSelf()));
            checkShellTask.execute();
        }

        private App getSelf() {
            PackageInfo Galaxy = new PackageInfo();
            Galaxy.applicationInfo = activity.getActivity().getApplicationInfo();
            Galaxy.packageName = BuildConfig.APPLICATION_ID;
            Galaxy.versionCode = BuildConfig.VERSION_CODE;
            return new App(Galaxy);
        }
    }
}
