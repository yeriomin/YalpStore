package in.dragons.galaxy.fragment.preference;

import android.Manifest;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.ListPreference;
import android.preference.Preference;
import android.widget.Toast;

import in.dragons.galaxy.BuildConfig;
import in.dragons.galaxy.PreferenceActivity;
import in.dragons.galaxy.R;
import in.dragons.galaxy.model.App;
import in.dragons.galaxy.task.CheckShellTask;
import in.dragons.galaxy.task.CheckSuTask;
import in.dragons.galaxy.task.ConvertToSystemTask;

class OnInstallationMethodChangeListener implements Preference.OnPreferenceChangeListener {

    private PreferenceActivity activity;

    public OnInstallationMethodChangeListener(PreferenceActivity activity) {
        this.activity = activity;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String oldValue = ((ListPreference) preference).getValue();
        if (null != oldValue && !oldValue.equals(newValue)) {
            if (PreferenceActivity.INSTALLATION_METHOD_PRIVILEGED.equals(newValue)) {
                if (!checkPrivileged()) {
                    return false;
                }
            } else if (PreferenceActivity.INSTALLATION_METHOD_ROOT.equals(newValue)) {
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

    private boolean checkPrivileged() {
        boolean privileged = activity.getPackageManager().checkPermission(Manifest.permission.INSTALL_PACKAGES, BuildConfig.APPLICATION_ID) == PackageManager.PERMISSION_GRANTED;
        if (!privileged) {
            new LocalCheckSuTask(activity).execute();
        }
        return privileged;
    }

    static class LocalCheckSuTask extends CheckSuTask {

        public LocalCheckSuTask(PreferenceActivity activity) {
            super(activity);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (!available) {
                Toast.makeText(activity.getApplicationContext(), R.string.pref_not_privileged, Toast.LENGTH_LONG).show();
                return;
            }
            showPrivilegedInstallationDialog();
        }

        private void showPrivilegedInstallationDialog() {
            CheckShellTask checkShellTask = new CheckShellTask(activity);
            checkShellTask.setPrimaryTask(new ConvertToSystemTask(activity, getSelf()));
            checkShellTask.execute();
        }

        private App getSelf() {
            PackageInfo yalp = new PackageInfo();
            yalp.applicationInfo = activity.getApplicationInfo();
            yalp.packageName = BuildConfig.APPLICATION_ID;
            yalp.versionCode = BuildConfig.VERSION_CODE;
            return new App(yalp);
        }
    }
}
