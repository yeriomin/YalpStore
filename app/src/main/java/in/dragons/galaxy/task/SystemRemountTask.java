package in.dragons.galaxy.task;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;
import in.dragons.galaxy.R;
import in.dragons.galaxy.model.App;

public abstract class SystemRemountTask extends TaskWithProgress<List<String>> {

    static private final String MOUNT_RW = "mount -o rw,remount,rw /system";
    static private final String MOUNT_RO = "mount -o ro,remount,ro /system";

    protected App app;
    protected boolean busybox;

    abstract protected List<String> getCommands();

    public SystemRemountTask(Context context, App app) {
        this.context = context;
        this.app = app;
    }

    public App getApp() {
        return app;
    }

    public void setBusybox(boolean busybox) {
        this.busybox = busybox;
    }

    @Override
    protected void onPreExecute() {
        prepareDialog(
                R.string.dialog_message_remounting_system,
                R.string.dialog_title_remounting_system
        );
        super.onPreExecute();
    }

    @Override
    protected List<String> doInBackground(String... params) {
        List<String> commands = new ArrayList<>();
        commands.add(MOUNT_RW);
        commands.addAll(getCommands());
        commands.add(MOUNT_RO);
        return Shell.SU.run(commands);
    }

    @Override
    protected void onPostExecute(List<String> output) {
        super.onPostExecute(output);
        if (null != output) {
            for (String outputLine : output) {
                Log.i(getClass().getSimpleName(), outputLine);
            }
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
