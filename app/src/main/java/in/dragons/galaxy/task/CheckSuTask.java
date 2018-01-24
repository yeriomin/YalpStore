package in.dragons.galaxy.task;

import android.os.AsyncTask;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;

import in.dragons.galaxy.ContextUtil;
import in.dragons.galaxy.PreferenceActivity;
import in.dragons.galaxy.R;

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
            ((CheckBoxPreference) activity.findPreference(PreferenceActivity.PREFERENCE_BACKGROUND_UPDATE_INSTALL)).setChecked(false);
            ((ListPreference) activity.findPreference(PreferenceActivity.PREFERENCE_INSTALLATION_METHOD)).setValueIndex(0);
            ContextUtil.toast(activity.getApplicationContext(), R.string.pref_no_root);
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        available = Shell.SU.available();
        return null;
    }
}
