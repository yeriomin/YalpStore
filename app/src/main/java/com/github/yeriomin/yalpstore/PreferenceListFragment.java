package com.github.yeriomin.yalpstore;

import android.preference.ListPreference;

import java.util.Map;

public abstract class PreferenceListFragment extends PreferenceFragment {

    protected ListPreference listPreference;

    abstract protected Map<String, String> getKeyValueMap();
    abstract protected OnListPreferenceChangeListener getOnListPreferenceChangeListener();

    public PreferenceListFragment(PreferenceActivity activity) {
        super(activity);
    }

    public void setListPreference(ListPreference listPreference) {
        this.listPreference = listPreference;
    }

    @Override
    public void draw() {
        final Map<String, String> keyValueMap = getKeyValueMap();
        int count = keyValueMap.size();
        listPreference.setEntries(keyValueMap.values().toArray(new CharSequence[count]));
        listPreference.setEntryValues(keyValueMap.keySet().toArray(new CharSequence[count]));
        OnListPreferenceChangeListener listener = getOnListPreferenceChangeListener();
        listener.setKeyValueMap(keyValueMap);
        listPreference.setOnPreferenceChangeListener(listener);
        listener.setSummary(listPreference, listPreference.getValue());
    }
}
