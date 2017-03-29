package com.github.yeriomin.yalpstore;

import android.preference.Preference;
import android.text.TextUtils;

import java.util.Map;

public abstract class OnListPreferenceChangeListener implements Preference.OnPreferenceChangeListener {

    protected Map<String, String> keyValueMap;
    protected String defaultLabel;

    public void setKeyValueMap(Map<String, String> keyValueMap) {
        this.keyValueMap = keyValueMap;
    }

    public void setDefaultLabel(String defaultLabel) {
        this.defaultLabel = defaultLabel;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        setSummary(preference, newValue);
        return true;
    }

    public void setSummary(Preference preference, Object newValue) {
        preference.setSummary(TextUtils.isEmpty((CharSequence) newValue) ? defaultLabel : keyValueMap.get(newValue));
    }
}
