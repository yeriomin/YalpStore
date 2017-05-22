package com.github.yeriomin.yalpstore.fragment.preference;

import android.preference.Preference;
import android.widget.Toast;

import com.github.yeriomin.yalpstore.OnListPreferenceChangeListener;
import com.github.yeriomin.yalpstore.PreferenceActivity;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.SpoofDeviceManager;
import com.github.yeriomin.yalpstore.Util;

import java.util.LinkedHashMap;
import java.util.Map;

public class Device extends List {

    public Device(PreferenceActivity activity) {
        super(activity);
    }

    @Override
    protected OnListPreferenceChangeListener getOnListPreferenceChangeListener() {
        OnListPreferenceChangeListener listener = new OnListPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean result = super.onPreferenceChange(preference, newValue);
                Toast.makeText(activity, R.string.pref_device_to_pretend_to_be_toast, Toast.LENGTH_LONG).show();
                return result;
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
}
