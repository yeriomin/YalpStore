package com.github.yeriomin.yalpstore;

import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

import eu.chainfire.libsuperuser.Shell;

public class InstallTask extends AsyncTask<String, Void, Boolean> {

    @Override
    protected Boolean doInBackground(String... params) {
        String file = params[0];
        Log.i(getClass().getName(), "Installing update " + file);
        List<String> lines = Shell.SU.run("pm install -i \"" + BuildConfig.APPLICATION_ID + "\" -r " + file);
        if (null != lines) {
            for (String line: lines) {
                Log.i(getClass().getName(), line);
            }
        }
        return null != lines && lines.size() == 1 && lines.get(0).equals("Success");
    }
}
