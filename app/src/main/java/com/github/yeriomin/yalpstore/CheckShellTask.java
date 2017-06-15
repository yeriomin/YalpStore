package com.github.yeriomin.yalpstore;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.chainfire.libsuperuser.Shell;

public class CheckShellTask extends AsyncTask<Void, Void, Boolean> {

    static private final int RETURN_CODE_NOT_FOUND = 127;
    static private final String COMMAND_ECHO = "echo ";
    static private final String COMMAND_RETURNED = " returned ";
    static private final String COMMAND_CODE = "$?";
    static private final String COMMAND_MV = "mv";
    static private final String COMMAND_RM = "rm";
    static private final String COMMAND_MKDIR = "mkdir";
    static private final String COMMAND_CHMOD = "chmod";
    static private final String COMMAND_CHOWN = "chown";
    static private final String COMMAND_CHGRP = "chgrp";
    static private final String COMMAND_BUSYBOX = "busybox";
    static private final String[] COMMANDS = new String[] {
        COMMAND_MV,
        COMMAND_RM,
        COMMAND_MKDIR,
        COMMAND_CHMOD,
        COMMAND_CHOWN,
        COMMAND_CHGRP,
        COMMAND_BUSYBOX
    };

    private Context context;
    private boolean availableCoreutils;
    private boolean availableBusybox;
    private ProgressDialog progressDialog;
    private SystemRemountTask primaryTask;

    public CheckShellTask(Context context) {
        this.context = context;
    }

    public void setPrimaryTask(SystemRemountTask primaryTask) {
        this.primaryTask = primaryTask;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (!Shell.SU.available()) {
            return false;
        }
        List<String> output = Shell.SU.run(getCommands());
        if (null == output) {
            return false;
        }
        Map<String, Boolean> flags = processOutput(output);
        availableCoreutils = flags.get(COMMAND_MV) && flags.get(COMMAND_RM) && flags.get(COMMAND_MKDIR) && flags.get(COMMAND_CHMOD);
        availableBusybox = flags.get(COMMAND_BUSYBOX);
        Log.i(getClass().getName(), "Coreutils available " + availableCoreutils);
        Log.i(getClass().getName(), "Busybox available " + availableBusybox);
        return true;
    }

    private List<String> getCommands() {
        List<String> commands = new ArrayList<>();
        for (String command: COMMANDS) {
            commands.add(command);
            commands.add(COMMAND_ECHO + command + COMMAND_RETURNED + COMMAND_CODE);
        }
        return commands;
    }

    private Map<String, Boolean> processOutput(List<String> output) {
        Map<String, Boolean> flags = new HashMap<>();
        for (String line: output) {
            Log.d(getClass().getName(), line);
            for (String command: COMMANDS) {
                if (line.contains(command + COMMAND_RETURNED)) {
                    int returnCode = Integer.parseInt(line.substring((command + COMMAND_RETURNED).length()).trim());
                    flags.put(command, returnCode != RETURN_CODE_NOT_FOUND);
                }
            }
        }
        return flags;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = Util.prepareProgressDialog(
            context,
            R.string.dialog_message_checking_busybox,
            R.string.dialog_title_checking_busybox
        );
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        progressDialog.dismiss();
        if (!result) {
            Toast.makeText(context, R.string.pref_no_root, Toast.LENGTH_LONG).show();
        } else if (!availableBusybox && !availableCoreutils) {
            showBusyboxDialog();
        } else {
            primaryTask.setBusybox(availableBusybox);
            askAndExecute(primaryTask);
        }
    }

    private void askAndExecute(SystemRemountTask task) {
        new SystemRemountDialogBuilder(context)
            .setPrimaryTask(task)
            .show()
        ;
    }

    private void showBusyboxDialog() {
        new AlertDialog.Builder(context)
            .setMessage(R.string.dialog_message_busybox_not_available)
            .setTitle(R.string.dialog_title_busybox_not_available)
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    context.startActivity(getBusyboxSearchIntent());
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

    private Intent getBusyboxSearchIntent() {
        Intent i = new Intent(context, SearchActivity.class);
        i.setAction(Intent.ACTION_SEARCH);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra(SearchManager.QUERY, "busybox");
        return i;
    }
}
