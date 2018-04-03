/*
 * Yalp Store
 * Copyright (C) 2018 Sergey Yeriomin <yeriomin@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

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
