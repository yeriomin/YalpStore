package com.github.yeriomin.yalpstore.task;

import android.os.AsyncTask;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;

import com.github.yeriomin.yalpstore.ContextUtil;
import com.github.yeriomin.yalpstore.PreferenceActivity;
import com.github.yeriomin.yalpstore.PreferenceUtil;
import com.github.yeriomin.yalpstore.R;

import eu.chainfire.libsuperuser.Shell;

public class CheckSuTask extends AsyncTask<Void, Void, Void> {

    protected PreferenceActivity activity;
    protected boolean available;

    public CheckSuTask(PreferenceActivity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (!available) {
            ((CheckBoxPreference) activity.findPreference(PreferenceUtil.PREFERENCE_BACKGROUND_UPDATE_INSTALL)).setChecked(false);
            ((ListPreference) activity.findPreference(PreferenceUtil.PREFERENCE_INSTALLATION_METHOD)).setValueIndex(0);
            ContextUtil.toast(activity.getApplicationContext(), R.string.pref_no_root);
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        available = Shell.SU.available();
        return null;
    }
}
