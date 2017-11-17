package com.github.yeriomin.yalpstore.task;

import android.os.AsyncTask;
import android.util.Log;

import com.github.yeriomin.yalpstore.BuildConfig;

import java.util.List;

import eu.chainfire.libsuperuser.Shell;

public class InstallTask extends AsyncTask<String, Void, Boolean> {

    @Override
    protected Boolean doInBackground(String... params) {
        String file = params[0];
        Log.i(getClass().getSimpleName(), "Installing update " + file);
        List<String> lines = Shell.SU.run("pm install -i \"" + BuildConfig.APPLICATION_ID + "\" -r " + file);
        if (null != lines) {
            for (String line: lines) {
                Log.i(getClass().getSimpleName(), line);
            }
        }
        return null != lines && lines.size() == 1 && lines.get(0).equals("Success");
    }
}
