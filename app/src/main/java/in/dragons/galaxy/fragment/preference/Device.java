package in.dragons.galaxy.fragment.preference;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.dragons.aurora.playstoreapiv2.PropertiesDeviceInfoProvider;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import in.dragons.galaxy.ContextUtil;
import in.dragons.galaxy.activities.DeviceInfoActivity;
import in.dragons.galaxy.activities.GalaxyActivity;
import in.dragons.galaxy.OnListPreferenceChangeListener;
import in.dragons.galaxy.PlayStoreApiAuthenticator;
import in.dragons.galaxy.fragment.PreferenceFragment;
import in.dragons.galaxy.R;
import in.dragons.galaxy.SpoofDeviceManager;
import in.dragons.galaxy.Util;

public class Device extends List {

    private static final String PREFERENCE_DEVICE_DEFINITION_REQUESTED = "PREFERENCE_DEVICE_DEFINITION_REQUESTED";

    public Device(PreferenceFragment activity) {
        super(activity);
    }

    @Override
    public void draw() {
        super.draw();
        listPreference.setOnPreferenceClickListener(preference -> {
            ContextUtil.toast(
                    activity.getActivity().getApplicationContext(),
                    R.string.pref_device_to_pretend_to_be_notice,
                    PreferenceManager.getDefaultSharedPreferences(activity.getActivity()).getString(PreferenceFragment.PREFERENCE_DOWNLOAD_DIRECTORY, "")
            );
            ((AlertDialog) listPreference.getDialog()).getListView().setOnItemLongClickListener((parent, v, position, id) -> {
                if (position > 0) {
                    Intent i = new Intent(activity.getActivity(), DeviceInfoActivity.class);
                    i.putExtra(DeviceInfoActivity.INTENT_DEVICE_NAME, (String) keyValueMap.keySet().toArray()[position]);
                    activity.startActivity(i);
                }
                return false;
            });
            return false;
        });
    }

    @Override
    protected OnListPreferenceChangeListener getOnListPreferenceChangeListener() {
        OnListPreferenceChangeListener listener = new OnListPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (!TextUtils.isEmpty((String) newValue) && !isDeviceDefinitionValid((String) newValue)) {
                    ContextUtil.toast(activity.getActivity().getApplicationContext(), R.string.error_invalid_device_definition);
                    return false;
                }
                showLogOutDialog();
                return super.onPreferenceChange(preference, newValue);
            }
        };
        listener.setDefaultLabel(activity.getString(R.string.pref_device_to_pretend_to_be_default));
        return listener;
    }

    @Override
    protected Map<String, String> getKeyValueMap() {
        Map<String, String> devices = new SpoofDeviceManager(activity.getActivity()).getDevices();
        devices = Util.sort(devices);
        Util.addToStart(
                (LinkedHashMap<String, String>) devices,
                "",
                activity.getString(R.string.pref_device_to_pretend_to_be_default)
        );
        return devices;
    }

    private boolean isDeviceDefinitionValid(String spoofDevice) {
        PropertiesDeviceInfoProvider deviceInfoProvider = new PropertiesDeviceInfoProvider();
        deviceInfoProvider.setProperties(new SpoofDeviceManager(activity.getActivity()).getProperties(spoofDevice));
        deviceInfoProvider.setLocaleString(Locale.getDefault().toString());
        return deviceInfoProvider.isValid();
    }

    private boolean showRequestDialog(boolean logOut) {
        PreferenceManager.getDefaultSharedPreferences(activity.getActivity())
                .edit()
                .putBoolean(PREFERENCE_DEVICE_DEFINITION_REQUESTED, true)
                .apply()
        ;
        return true;
    }

    private AlertDialog showLogOutDialog() {
        return new AlertDialog.Builder(activity.getActivity())
                .setMessage(R.string.pref_device_to_pretend_to_be_toast)
                .setTitle(R.string.dialog_title_logout)
                .setPositiveButton(android.R.string.yes, new RequestOnClickListener(activity.getActivity(), true))
                .setNegativeButton(R.string.dialog_two_factor_cancel, new RequestOnClickListener(activity.getActivity(), false))
                .show()
                ;
    }

    private void finishAll() {
        new PlayStoreApiAuthenticator(activity.getActivity().getApplicationContext()).logout();
        GalaxyActivity.cascadeFinish();
        activity.getActivity().finish();
    }

    class RequestOnClickListener implements DialogInterface.OnClickListener {

        private boolean logOut;
        private boolean askedAlready;

        public RequestOnClickListener(Activity activity, boolean logOut) {
            askedAlready = PreferenceFragment.getBoolean(activity, PREFERENCE_DEVICE_DEFINITION_REQUESTED);
            this.logOut = logOut;
        }

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
            if (askedAlready) {
                if (logOut) {
                    finishAll();
                }
            } else {
                showRequestDialog(logOut);
            }
        }
    }

    class FinishingOnClickListener implements DialogInterface.OnClickListener {

        private boolean logOut;

        public FinishingOnClickListener(boolean logOut) {
            this.logOut = logOut;
        }

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
            if (logOut) {
                finishAll();
            }
        }
    }
}
