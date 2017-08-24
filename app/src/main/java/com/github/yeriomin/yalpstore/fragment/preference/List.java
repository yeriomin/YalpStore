package com.github.yeriomin.yalpstore.fragment.preference;

import android.os.AsyncTask;
import android.preference.ListPreference;

import com.github.yeriomin.yalpstore.OnListPreferenceChangeListener;
import com.github.yeriomin.yalpstore.PreferenceActivity;

import java.util.Map;

public abstract class List extends Abstract {

    protected ListPreference listPreference;
    protected Map<String, String> keyValueMap;

    abstract protected Map<String, String> getKeyValueMap();
    abstract protected OnListPreferenceChangeListener getOnListPreferenceChangeListener();

    public List(PreferenceActivity activity) {
        super(activity);
    }

    public void setListPreference(ListPreference listPreference) {
        this.listPreference = listPreference;
    }

    @Override
    public void draw() {
        new ListValuesTask(this, listPreference).execute();
    }

    static class ListValuesTask extends AsyncTask<Void, Void, Void> {

        private List list;
        private ListPreference listPreference;

        public ListValuesTask(List list, ListPreference listPreference) {
            this.list = list;
            this.listPreference = listPreference;
        }

        @Override
        protected void onPreExecute() {
            listPreference.setEntries(new String[0]);
            listPreference.setEntryValues(new String[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            int count = list.keyValueMap.size();
            listPreference.setEntries(list.keyValueMap.values().toArray(new CharSequence[count]));
            listPreference.setEntryValues(list.keyValueMap.keySet().toArray(new CharSequence[count]));
            OnListPreferenceChangeListener listener = list.getOnListPreferenceChangeListener();
            listener.setKeyValueMap(list.keyValueMap);
            listPreference.setOnPreferenceChangeListener(listener);
            listener.setSummary(listPreference, listPreference.getValue());
        }

        @Override
        protected Void doInBackground(Void... voids) {
            list.keyValueMap = list.getKeyValueMap();
            return null;
        }
    }
}
