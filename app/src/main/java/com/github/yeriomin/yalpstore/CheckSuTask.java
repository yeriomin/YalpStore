package com.github.yeriomin.yalpstore;

import android.os.AsyncTask;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.widget.Toast;

import eu.chainfire.libsuperuser.Shell;

public class CheckSuTask extends AsyncTask<Void, Void, Void> {

    private PreferenceActivity activity;

    protected boolean available;

    public CheckSuTask(PreferenceActivity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (!available) {
            ((CheckBoxPreference) activity.findPreference(PreferenceActivity.PREFERENCE_BACKGROUND_UPDATE_INSTALL)).setChecked(false);
            ((ListPreference) activity.findPreference(PreferenceActivity.PREFERENCE_INSTALLATION_METHOD)).setValueIndex(0);
            Toast.makeText(activity, activity.getString(R.string.pref_no_root), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        available = Shell.SU.available();
        return null;
    }
}
