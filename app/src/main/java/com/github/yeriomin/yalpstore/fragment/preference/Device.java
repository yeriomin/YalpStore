package com.github.yeriomin.yalpstore.fragment.preference;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.github.yeriomin.playstoreapi.PropertiesDeviceInfoProvider;
import com.github.yeriomin.yalpstore.OnListPreferenceChangeListener;
import com.github.yeriomin.yalpstore.Paths;
import com.github.yeriomin.yalpstore.PlayStoreApiAuthenticator;
import com.github.yeriomin.yalpstore.PreferenceActivity;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.SpoofDeviceManager;
import com.github.yeriomin.yalpstore.Util;
import com.github.yeriomin.yalpstore.YalpStoreActivity;
import com.github.yeriomin.yalpstore.bugreport.BugReportService;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class Device extends List {

    public Device(PreferenceActivity activity) {
        super(activity);
    }

    @Override
    public void draw() {
        super.draw();
        listPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Toast.makeText(
                    activity.getApplicationContext(),
                    activity.getString(R.string.pref_device_to_pretend_to_be_notice, Paths.getYalpPath().getName()),
                    Toast.LENGTH_LONG
                ).show();
                return false;
            }
        });
    }

    @Override
    protected OnListPreferenceChangeListener getOnListPreferenceChangeListener() {
        OnListPreferenceChangeListener listener = new OnListPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (!TextUtils.isEmpty((String) newValue) && !isDeviceDefinitionValid((String) newValue)) {
                    Toast.makeText(activity, R.string.error_invalid_device_definition, Toast.LENGTH_LONG).show();
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
        Map<String, String> devices = new SpoofDeviceManager(activity).getDevices();
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
        deviceInfoProvider.setProperties(new SpoofDeviceManager(activity).getProperties(spoofDevice));
        deviceInfoProvider.setLocaleString(Locale.getDefault().toString());
        return deviceInfoProvider.isValid();
    }

    private AlertDialog showRequestDialog(boolean logOut) {
        PreferenceManager.getDefaultSharedPreferences(activity)
            .edit()
            .putBoolean(PreferenceActivity.PREFERENCE_DEVICE_DEFINITION_REQUESTED, true)
            .commit()
        ;
        return new AlertDialog.Builder(activity)
            .setMessage(R.string.dialog_message_spoof_request)
            .setTitle(R.string.dialog_title_spoof_request)
            .setPositiveButton(android.R.string.yes, new FinishingOnClickListener(logOut) {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    send();
                    Toast.makeText(
                        activity.getApplicationContext(),
                        activity.getString(R.string.thank_you),
                        Toast.LENGTH_SHORT
                    ).show();
                    super.onClick(dialogInterface, i);
                }
            })
            .setNegativeButton(android.R.string.no, new FinishingOnClickListener(logOut))
            .show()
        ;
    }

    private void send() {
        Intent intentBugReport = new Intent(activity.getApplicationContext(), BugReportService.class);
        intentBugReport.setAction(BugReportService.ACTION_SEND_FTP);
        intentBugReport.putExtra(BugReportService.INTENT_MESSAGE, activity.getString(R.string.sent_from_device_definition_dialog));
        activity.startService(intentBugReport);
    }

    private AlertDialog showLogOutDialog() {
        return new AlertDialog.Builder(activity)
            .setMessage(R.string.pref_device_to_pretend_to_be_toast)
            .setTitle(R.string.dialog_title_logout)
            .setPositiveButton(android.R.string.yes, new RequestOnClickListener(activity, true))
            .setNegativeButton(R.string.dialog_two_factor_cancel, new RequestOnClickListener(activity, false))
            .show()
        ;
    }

    private void finishAll() {
        new PlayStoreApiAuthenticator(activity.getApplicationContext()).logout();
        YalpStoreActivity.cascadeFinish();
        activity.finish();
    }

    class RequestOnClickListener implements DialogInterface.OnClickListener {

        private boolean logOut;
        private boolean askedAlready;

        public RequestOnClickListener(Activity activity, boolean logOut) {
            askedAlready = PreferenceActivity.getBoolean(activity, PreferenceActivity.PREFERENCE_DEVICE_DEFINITION_REQUESTED);
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
