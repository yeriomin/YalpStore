package com.dragons.aurora.fragment.details;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.dragons.aurora.BlackWhiteListManager;
import com.dragons.aurora.BuildConfig;
import com.dragons.aurora.ContextUtil;
import com.dragons.aurora.InstalledApkCopier;
import com.dragons.aurora.R;
import com.dragons.aurora.activities.AuroraActivity;
import com.dragons.aurora.activities.ManualDownloadActivity;
import com.dragons.aurora.builders.FlagDialogBuilder;
import com.dragons.aurora.model.App;
import com.dragons.aurora.task.CheckShellTask;
import com.dragons.aurora.task.ConvertToNormalTask;
import com.dragons.aurora.task.ConvertToSystemTask;
import com.dragons.aurora.task.SystemRemountTask;

public class DownloadOptions extends Abstract {

    public DownloadOptions(AuroraActivity activity, App app) {
        super(activity, app);
    }

    @Override
    public void draw() {
    }

    public void inflate(Menu menu) {
        MenuInflater inflater = activity.getMenuInflater();
        inflater.inflate(R.menu.menu_download, menu);
        if (!isInstalled(app)) {
            return;
        }
        menu.findItem(R.id.action_get_local_apk).setVisible(true);
        BlackWhiteListManager manager = new BlackWhiteListManager(activity);
        boolean isContained = manager.contains(app.getPackageName());
        if (manager.isBlack()) {
            menu.findItem(R.id.action_unignore).setVisible(isContained);
            menu.findItem(R.id.action_ignore).setVisible(!isContained);
        } else {
            menu.findItem(R.id.action_unwhitelist).setVisible(isContained);
            menu.findItem(R.id.action_whitelist).setVisible(!isContained);
        }
        if (isConvertible(app)) {
            menu.findItem(R.id.action_make_system).setVisible(!app.isSystem());
            menu.findItem(R.id.action_make_normal).setVisible(app.isSystem());
        }
        menu.findItem(R.id.action_flag).setVisible(app.isInstalled());
    }

    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_ignore:
            case R.id.action_whitelist:
                new BlackWhiteListManager(activity).add(app.getPackageName());
                draw();
                return true;
            case R.id.action_unignore:
            case R.id.action_unwhitelist:
                new BlackWhiteListManager(activity).remove(app.getPackageName());
                draw();
                return true;
            case R.id.action_manual:
                ManualDownloadActivity.app = app;
                activity.startActivity(new Intent(activity, ManualDownloadActivity.class));
                return true;
            case R.id.action_get_local_apk:
                new CopyTask(activity).execute(app);
                return true;
            case R.id.action_make_system:
                checkAndExecute(new ConvertToSystemTask(activity, app));
                return true;
            case R.id.action_make_normal:
                checkAndExecute(new ConvertToNormalTask(activity, app));
                return true;
            case R.id.action_flag:
                new FlagDialogBuilder().setActivity(activity).setApp(app).build().show();
                return true;
            default:
                return activity.onContextItemSelected(item);
        }
    }

    private void checkAndExecute(SystemRemountTask primaryTask) {
        CheckShellTask checkShellTask = new CheckShellTask(activity);
        checkShellTask.setPrimaryTask(primaryTask);
        checkShellTask.execute();
    }

    private boolean isConvertible(App app) {
        return isInstalled(app)
                && !app.getPackageName().equals(BuildConfig.APPLICATION_ID)
                && null != app.getPackageInfo().applicationInfo
                && null != app.getPackageInfo().applicationInfo.sourceDir
                && !app.getPackageInfo().applicationInfo.sourceDir.endsWith("pkg.apk")
                ;
    }

    private boolean isInstalled(App app) {
        try {
            activity.getPackageManager().getPackageInfo(app.getPackageName(), 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    static class CopyTask extends AsyncTask<App, Void, Boolean> {

        @SuppressLint("StaticFieldLeak")
        private Activity activity;
        private App app;

        CopyTask(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            ContextUtil.toastLong(
                    activity.getApplicationContext(),
                    activity.getString(result
                            ? R.string.details_saved_in_downloads
                            : R.string.details_could_not_copy_apk
                    )
            );
        }

        @Override
        protected Boolean doInBackground(App... apps) {
            app = apps[0];
            return InstalledApkCopier.copy(activity, app);
        }
    }
}
