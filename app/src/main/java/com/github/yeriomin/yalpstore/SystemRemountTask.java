package com.github.yeriomin.yalpstore;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import com.github.yeriomin.yalpstore.model.App;

import java.util.ArrayList;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;

public abstract class SystemRemountTask extends AsyncTask<Void, Void, List<String>> {

    static private final String MOUNT_RW = "mount -o rw,remount,rw /system";
    static private final String MOUNT_RO = "mount -o ro,remount,ro /system";
    static private final String FORCE_STOP = "am force-stop ";

    protected Context context;
    protected App app;
    protected boolean busybox;

    private ProgressDialog progressDialog;

    abstract protected List<String> getCommands();

    public SystemRemountTask(Context context, App app) {
        this.context = context;
        this.app = app;
    }

    public void setBusybox(boolean busybox) {
        this.busybox = busybox;
    }

    @Override
    protected void onPreExecute() {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle(R.string.dialog_title_remounting_system);
        dialog.setMessage(context.getString(R.string.dialog_message_remounting_system));
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        progressDialog = dialog;
        progressDialog.show();
    }

    @Override
    protected List<String> doInBackground(Void[] params) {
        List<String> commands = new ArrayList<>();
        commands.add(MOUNT_RW);
        commands.add(FORCE_STOP + app.getPackageName());
        commands.addAll(getCommands());
        commands.add(MOUNT_RO);
        return Shell.SU.run(commands);
    }

    @Override
    protected void onPostExecute(List<String> output) {
        progressDialog.dismiss();
        for (String outputLine: output) {
            Log.i(getClass().getName(), outputLine);
        }
        showRebootDialog();
    }

    protected String getBusyboxCommand(String command) {
        return (busybox ? "busybox " : "") + command;
    }

    private void showRebootDialog() {
        new AlertDialog.Builder(context)
            .setMessage(R.string.dialog_message_reboot_required)
            .setTitle(R.string.dialog_title_reboot_required)
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new RebootTask().execute();
                    dialog.dismiss();
                }
            })
            .setNegativeButton(R.string.dialog_two_factor_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            })
            .show()
        ;
    }
}
