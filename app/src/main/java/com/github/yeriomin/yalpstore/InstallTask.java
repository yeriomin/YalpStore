package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import eu.chainfire.libsuperuser.Shell;

public class InstallTask extends AsyncTask<String, Void, Void> {

    private Context context;
    private String appDisplayName;

    public InstallTask(Context context, String appDisplayName) {
        this.context = context;
        this.appDisplayName = appDisplayName;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        new NotificationUtil(context).show(
            new Intent(),
            appDisplayName,
            context.getString(R.string.notification_installation_complete)
        );
        Toast.makeText(
            context,
            context.getString(R.string.notification_installation_complete_toast, appDisplayName),
            Toast.LENGTH_LONG
        ).show();
    }

    @Override
    protected Void doInBackground(String... params) {
        String file = params[0];
        Log.i(getClass().getName(), "Installing update " + file);
        for (String line: Shell.SU.run("pm install -r " + file)) {
            Log.i(getClass().getName(), line);
        }
        return null;
    }
}
