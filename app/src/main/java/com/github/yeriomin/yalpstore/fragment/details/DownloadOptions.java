package com.github.yeriomin.yalpstore.fragment.details;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.github.yeriomin.yalpstore.BuildConfig;
import com.github.yeriomin.yalpstore.CheckShellTask;
import com.github.yeriomin.yalpstore.ConvertToNormalTask;
import com.github.yeriomin.yalpstore.ConvertToSystemTask;
import com.github.yeriomin.yalpstore.DetailsActivity;
import com.github.yeriomin.yalpstore.InstalledApkCopier;
import com.github.yeriomin.yalpstore.ManualDownloadActivity;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.SystemRemountTask;
import com.github.yeriomin.yalpstore.model.App;

public class DownloadOptions extends Abstract {

    public DownloadOptions(DetailsActivity activity, App app) {
        super(activity, app);
    }

    @Override
    public void draw() {
        final ImageButton more = activity.findViewById(R.id.more);
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
                new CopyTask(activity).execute(app);
                return true;
            case R.id.action_make_system:
                checkAndExecute(new ConvertToSystemTask(activity, app));
                return true;
            case R.id.action_make_normal:
                checkAndExecute(new ConvertToNormalTask(activity, app));
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
            String message = activity.getString(InstalledApkCopier.copy(app)
                ? R.string.details_saved_in_downloads
                : R.string.details_could_not_copy_apk
            );
            Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
        }

        @Override
        protected Boolean doInBackground(App... apps) {
            app = apps[0];
            return InstalledApkCopier.copy(app);
        }
    }
}
