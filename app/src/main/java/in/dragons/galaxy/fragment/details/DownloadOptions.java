package in.dragons.galaxy.fragment.details;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import in.dragons.galaxy.BlackWhiteListManager;
import in.dragons.galaxy.BuildConfig;
import in.dragons.galaxy.ContextUtil;
import in.dragons.galaxy.FlagDialogBuilder;
import in.dragons.galaxy.InstalledApkCopier;
import in.dragons.galaxy.ManualDownloadActivity;
import in.dragons.galaxy.R;
import in.dragons.galaxy.GalaxyActivity;
import in.dragons.galaxy.model.App;
import in.dragons.galaxy.task.CheckShellTask;
import in.dragons.galaxy.task.ConvertToNormalTask;
import in.dragons.galaxy.task.ConvertToSystemTask;
import in.dragons.galaxy.task.SystemRemountTask;

public class DownloadOptions extends Abstract {

    public DownloadOptions(GalaxyActivity activity, App app) {
        super(activity, app);
    }

    @Override
    public void draw() {
        final ImageButton more =(ImageButton) activity.findViewById(R.id.icon);
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

        private Activity activity;
        private App app;

        public CopyTask(Activity activity) {
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
