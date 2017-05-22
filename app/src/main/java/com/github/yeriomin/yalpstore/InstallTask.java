package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.github.yeriomin.yalpstore.notification.NotificationManagerFactory;

import java.util.List;

import eu.chainfire.libsuperuser.Shell;

public class InstallTask extends AsyncTask<String, Void, Boolean> {

    private Context context;
    private String appDisplayName;

    public InstallTask(Context context, String appDisplayName) {
        this.context = context;
        this.appDisplayName = appDisplayName;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        NotificationManagerFactory.get(context).show(
            new Intent(),
            appDisplayName,
            context.getString(
                result
                ? R.string.notification_installation_complete
                : R.string.notification_installation_failed
            )
        );
        Toast.makeText(
            context,
            context.getString(
                result
                ? R.string.notification_installation_complete_toast
                : R.string.notification_installation_failed_toast,
                appDisplayName
            ),
            Toast.LENGTH_LONG
        ).show();
    }

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
