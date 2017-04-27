package com.github.yeriomin.yalpstore;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.ListPreference;
import android.preference.Preference;
import android.widget.Toast;

import com.github.yeriomin.yalpstore.model.App;

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
                return checkPrivileged();
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
            getPrivilegedCheckSuTask().execute();
        }
        return privileged;
    }

    private CheckSuTask getPrivilegedCheckSuTask() {
        return new CheckSuTask(activity) {

            @Override
            protected void onPostExecute(Void aVoid) {
                if (!available) {
                    Toast.makeText(activity, R.string.pref_not_privileged, Toast.LENGTH_LONG).show();
                    return;
                }
                showPrivilegedInstallationDialog();
            }
        };
    }

    private void showPrivilegedInstallationDialog() {
        final CheckShellTask checkTask = new CheckShellTask(activity);
        checkTask.setPrimaryTask(new ConvertToSystemTask(activity, getSelf()));
        new AlertDialog.Builder(activity)
            .setMessage(R.string.dialog_message_system_app_self)
            .setTitle(R.string.dialog_title_system_app_self)
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    checkTask.execute();
                    dialog.dismiss();
                }
            })
            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            })
            .show()
        ;
    }

    private App getSelf() {
        PackageInfo yalp = new PackageInfo();
        yalp.applicationInfo = activity.getApplicationInfo();
        yalp.packageName = BuildConfig.APPLICATION_ID;
        yalp.versionCode = BuildConfig.VERSION_CODE;
        return new App(yalp);
    }
}
