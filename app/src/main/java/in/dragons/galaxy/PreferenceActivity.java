package in.dragons.galaxy;

import android.content.Context;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.util.Log;

import in.dragons.galaxy.fragment.preference.Blacklist;
import in.dragons.galaxy.fragment.preference.CheckUpdates;
import in.dragons.galaxy.fragment.preference.Device;
import in.dragons.galaxy.fragment.preference.DownloadDirectory;
import in.dragons.galaxy.fragment.preference.InstallationMethod;
import in.dragons.galaxy.fragment.preference.Language;

public class PreferenceActivity extends android.preference.PreferenceActivity {

    public static final String PREFERENCE_AUTO_INSTALL = "PREFERENCE_AUTO_INSTALL";
    public static final String PREFERENCE_UPDATE_LIST_WHITE_OR_BLACK = "PREFERENCE_UPDATE_LIST_WHITE_OR_BLACK";
    public static final String PREFERENCE_UPDATE_LIST = "PREFERENCE_UPDATE_LIST";
    public static final String PREFERENCE_BACKGROUND_UPDATE_INTERVAL = "PREFERENCE_BACKGROUND_UPDATE_INTERVAL";
    public static final String PREFERENCE_DELETE_APK_AFTER_INSTALL = "PREFERENCE_DELETE_APK_AFTER_INSTALL";
    public static final String PREFERENCE_BACKGROUND_UPDATE_DOWNLOAD = "PREFERENCE_BACKGROUND_UPDATE_DOWNLOAD";
    public static final String PREFERENCE_BACKGROUND_UPDATE_WIFI_ONLY = "PREFERENCE_BACKGROUND_UPDATE_WIFI_ONLY";
    public static final String PREFERENCE_BACKGROUND_UPDATE_INSTALL = "PREFERENCE_BACKGROUND_UPDATE_INSTALL";
    public static final String PREFERENCE_REQUESTED_LANGUAGE = "PREFERENCE_REQUESTED_LANGUAGE";
    public static final String PREFERENCE_DEVICE_TO_PRETEND_TO_BE = "PREFERENCE_DEVICE_TO_PRETEND_TO_BE";
    public static final String PREFERENCE_INSTALLATION_METHOD = "PREFERENCE_INSTALLATION_METHOD";
    public static final String PREFERENCE_NO_IMAGES = "PREFERENCE_NO_IMAGES";
    public static final String PREFERENCE_DOWNLOAD_DIRECTORY = "PREFERENCE_DOWNLOAD_DIRECTORY";
    public static final String PREFERENCE_DOWNLOAD_DELTAS = "PREFERENCE_DOWNLOAD_DELTAS";
    public static final String PREFERENCE_AUTO_WHITELIST = "PREFERENCE_AUTO_WHITELIST";

    public static final String INSTALLATION_METHOD_DEFAULT = "default";
    public static final String INSTALLATION_METHOD_ROOT = "root";
    public static final String INSTALLATION_METHOD_PRIVILEGED = "privileged";

    public static final String LIST_BLACK = "black";
    
    static public boolean getBoolean(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, false);
    }

    static public String getString(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, "");
    }

    static public int getUpdateInterval(Context context) {
        return Util.parseInt(
                PreferenceManager.getDefaultSharedPreferences(context).getString(
                        PreferenceActivity.PREFERENCE_BACKGROUND_UPDATE_INTERVAL,
                        "-1"
                ),
                -1
        );
    }

    static public boolean canInstallInBackground(Context context) {
        return getString(context, PREFERENCE_INSTALLATION_METHOD).equals(INSTALLATION_METHOD_ROOT)
                || getString(context, PREFERENCE_INSTALLATION_METHOD).equals(INSTALLATION_METHOD_PRIVILEGED)
                ;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        setContentView(R.layout.preference_activity_layout);

        drawBlackList();
        drawLanguages();
        drawUpdatesCheck();
        drawDevices();
        drawInstallationMethod();
        new DownloadDirectory(this).setPreference((EditTextPreference) findPreference(PREFERENCE_DOWNLOAD_DIRECTORY)).draw();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (!GalaxyPermissionManager.isGranted(requestCode, permissions, grantResults)) {
            Log.i(getClass().getSimpleName(), "User denied the write permission");
            finish();
        }
    }

    private void drawBlackList() {
        Blacklist blacklistFragment = new Blacklist(this);
        blacklistFragment.setBlackOrWhite((ListPreference) findPreference(PREFERENCE_UPDATE_LIST_WHITE_OR_BLACK));
        blacklistFragment.setAppList((MultiSelectListPreference) findPreference(PREFERENCE_UPDATE_LIST));
        blacklistFragment.setAutoWhitelist((CheckBoxPreference) findPreference(PREFERENCE_AUTO_WHITELIST));
        blacklistFragment.draw();
    }

    private void drawUpdatesCheck() {
        CheckUpdates checkUpdatesFragment = new CheckUpdates(this);
        checkUpdatesFragment.setCheckForUpdates((ListPreference) findPreference(PREFERENCE_BACKGROUND_UPDATE_INTERVAL));
        checkUpdatesFragment.setAlsoInstall((CheckBoxPreference) findPreference(PREFERENCE_BACKGROUND_UPDATE_INSTALL));
        checkUpdatesFragment.setAlsoDownload((CheckBoxPreference) findPreference(PREFERENCE_BACKGROUND_UPDATE_DOWNLOAD));
        checkUpdatesFragment.draw();
    }

    private void drawLanguages() {
        Language languageFragment = new Language(this);
        languageFragment.setListPreference((ListPreference) findPreference(PREFERENCE_REQUESTED_LANGUAGE));
        languageFragment.draw();
    }

    private void drawDevices() {
        Device languageFragment = new Device(this);
        languageFragment.setListPreference((ListPreference) findPreference(PREFERENCE_DEVICE_TO_PRETEND_TO_BE));
        languageFragment.draw();
    }

    private void drawInstallationMethod() {
        InstallationMethod installationMethodFragment = new InstallationMethod(this);
        installationMethodFragment.setInstallationMethodPreference((ListPreference) findPreference(PREFERENCE_INSTALLATION_METHOD));
        installationMethodFragment.draw();
    }
}