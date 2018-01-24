package in.dragons.galaxy.fragment.preference;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.util.Log;

import in.dragons.galaxy.ContextUtil;
import in.dragons.galaxy.Paths;
import in.dragons.galaxy.PreferenceActivity;
import in.dragons.galaxy.R;
import in.dragons.galaxy.YalpStorePermissionManager;

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
        preference.setSummary(Paths.getYalpPath(activity).getAbsolutePath());
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                YalpStorePermissionManager permissionManager = new YalpStorePermissionManager(activity);
                if (!permissionManager.checkPermission()) {
                    permissionManager.requestPermission();
                }
                return true;
            }
        });
        preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                String newValue = (String) o;
                boolean result = checkNewValue(newValue);
                if (!result) {
                    if (ContextUtil.isAlive(activity) && !((EditTextPreference) preference).getText().equals(Paths.FALLBACK_DIRECTORY)) {
                        getFallbackDialog().show();
                    } else {
                        ContextUtil.toast(activity, R.string.error_downloads_directory_not_writable);
                    }
                } else {
                    try {
                        preference.setSummary(new File(Paths.getStorageRoot(activity), newValue).getCanonicalPath());
                    } catch (IOException e) {
                        Log.i(getClass().getName(), "checkNewValue returned true, but drawing the path \"" + newValue + "\" in the summary failed... strange");
                        return false;
                    }
                }
                return result;
            }

            private boolean checkNewValue(String newValue) {
                try {
                    File storageRoot = Paths.getStorageRoot(activity);
                    File newDir = new File(storageRoot, newValue).getCanonicalFile();
                    if (!newDir.getCanonicalPath().startsWith(storageRoot.getCanonicalPath())) {
                        return false;
                    }
                    if (newDir.exists()) {
                        return newDir.canWrite();
                    }
                    if (activity.checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        return newDir.mkdirs();
                    }
                    return true;
                } catch (IOException e) {
                    return false;
                }
            }

            private AlertDialog getFallbackDialog() {
                return new AlertDialog.Builder(activity)
                    .setMessage(
                        activity.getString(R.string.error_downloads_directory_not_writable)
                            + "\n\n"
                            + activity.getString(R.string.pref_message_fallback, Paths.FALLBACK_DIRECTORY)
                    )
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            preference.setText(Paths.FALLBACK_DIRECTORY);
                            preference.getOnPreferenceChangeListener().onPreferenceChange(preference, Paths.FALLBACK_DIRECTORY);
                            dialog.dismiss();
                        }
                    })
                    .create()
                ;
            }
        });
    }

    public DownloadDirectory(PreferenceActivity activity) {
        super(activity);
    }
}
