package com.github.yeriomin.yalpstore;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.preference.CheckBoxPreference;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.github.yeriomin.yalpstore.model.App;

public class DownloadOptionsFragment extends DetailsFragment {

    public DownloadOptionsFragment(DetailsActivity activity, App app) {
        super(activity, app);
    }

    @Override
    public void draw() {
        final ImageButton more = (ImageButton) activity.findViewById(R.id.more);
        if (null == more) {
            return;
        }
        activity.registerForContextMenu(more);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                more.showContextMenu();
            }
        });
    }

    public void inflate(Menu menu) {
        MenuInflater inflater = activity.getMenuInflater();
        inflater.inflate(R.menu.menu_download, menu);
        if (!app.isInstalled()) {
            return;
        }
        menu.findItem(R.id.action_get_local_apk).setVisible(true);
        if (isConvertible(app)) {
            menu.findItem(R.id.action_make_system).setVisible(!app.isSystem());
            menu.findItem(R.id.action_make_normal).setVisible(app.isSystem());
        }
    }

    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_manual:
                activity.startActivity(new Intent(activity, ManualDownloadActivity.class));
                return true;
            case R.id.action_get_local_apk:
                copyLocalApk();
                return true;
            case R.id.action_make_system:
                askAndExecute(new ConvertToSystemTask(activity, app));
                return true;
            case R.id.action_make_normal:
                askAndExecute(new ConvertToNormalTask(activity, app));
                return true;
            default:
                return activity.onContextItemSelected(item);
        }
    }

    private void copyLocalApk() {
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPostExecute(Boolean result) {
                String message = activity.getString(InstalledApkCopier.copy(app)
                    ? R.string.details_saved_in_downloads
                    : R.string.details_could_not_copy_apk
                );
                Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                return InstalledApkCopier.copy(app);
            }
        };
        task.execute();
    }

    private void askAndExecute(SystemRemountTask task) {
        final CheckShellTask checkTask = new CheckShellTask(activity);
        checkTask.setPrimaryTask(task);
        new AlertDialog.Builder(activity)
            .setMessage(task instanceof ConvertToSystemTask
                ? R.string.dialog_message_system_app_warning_to_system
                : R.string.dialog_message_system_app_warning_to_normal
            )
            .setTitle(R.string.dialog_title_system_app_warning)
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    checkTask.execute();
                    dialog.dismiss();
                }
            })
            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            })
            .show()
        ;
    }

    private boolean isConvertible(App app) {
        return !app.getPackageName().equals(BuildConfig.APPLICATION_ID)
            && !app.getPackageInfo().applicationInfo.sourceDir.endsWith("pkg.apk")
        ;
    }
}
