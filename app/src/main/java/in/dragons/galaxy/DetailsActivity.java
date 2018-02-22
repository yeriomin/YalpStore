package in.dragons.galaxy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;

import in.dragons.galaxy.fragment.details.AppLists;
import in.dragons.galaxy.fragment.details.BackToPlayStore;
import in.dragons.galaxy.fragment.details.Beta;
import in.dragons.galaxy.fragment.details.DownloadOptions;
import in.dragons.galaxy.fragment.details.DownloadOrInstall;
import in.dragons.galaxy.fragment.details.GeneralDetails;
import in.dragons.galaxy.fragment.details.Permissions;
import in.dragons.galaxy.fragment.details.Review;
import in.dragons.galaxy.fragment.details.Screenshot;
import in.dragons.galaxy.fragment.details.Share;
import in.dragons.galaxy.fragment.details.SystemAppPage;
import in.dragons.galaxy.fragment.details.Video;
import in.dragons.galaxy.model.App;
import in.dragons.galaxy.task.playstore.CloneableTask;
import in.dragons.galaxy.task.playstore.DetailsTask;

public class DetailsActivity extends GalaxyActivity {

    static private final String INTENT_PACKAGE_NAME = "INTENT_PACKAGE_NAME";

    static public App app;

    protected DownloadOrInstall downloadOrInstallFragment;

    static public Intent getDetailsIntent(Context context, String packageName) {
        Intent intent = new Intent(context, DetailsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(DetailsActivity.INTENT_PACKAGE_NAME, packageName);
        return intent;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        final String packageName = getIntentPackageName(intent);
        if (TextUtils.isEmpty(packageName)) {
            Log.e(this.getClass().getName(), "No package name provided");
            finish();
            return;
        }
        Log.i(getClass().getSimpleName(), "Getting info about " + packageName);

        if (null != DetailsActivity.app) {
            redrawDetails(DetailsActivity.app);
        }

        GetAndRedrawDetailsTask task = new GetAndRedrawDetailsTask(this);
        task.setPackageName(packageName);
        task.setProgressIndicator(findViewById(R.id.progress));
        task.execute();
    }

    @Override
    protected void onPause() {
        if (null != downloadOrInstallFragment) {
            downloadOrInstallFragment.unregisterReceivers();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        redrawButtons();
        super.onResume();
    }

    private void redrawButtons() {
        if (null != downloadOrInstallFragment) {
            downloadOrInstallFragment.unregisterReceivers();
            downloadOrInstallFragment.registerReceivers();
            downloadOrInstallFragment.draw();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        setTheme(sharedPreferences.getBoolean("THEME", true) ? R.style.AppTheme : R.style.AppTheme_Dark);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_activity_layout);
        onNewIntent(getIntent());
        onCreateDrawer(savedInstanceState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (GalaxyPermissionManager.isGranted(requestCode, permissions, grantResults)) {
            Log.i(getClass().getSimpleName(), "User granted the write permission");
            if (null == downloadOrInstallFragment && null != app) {
                downloadOrInstallFragment = new DownloadOrInstall(this, app);
                redrawButtons();
            }
            if (null != downloadOrInstallFragment) {
                downloadOrInstallFragment.download();
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        new DownloadOptions(this, app).inflate(menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return new DownloadOptions(this, app).onContextItemSelected(item);
    }

    private String getIntentPackageName(Intent intent) {
        if (intent.hasExtra(INTENT_PACKAGE_NAME)) {
            return intent.getStringExtra(INTENT_PACKAGE_NAME);
        } else if (intent.getScheme() != null
                && (intent.getScheme().equals("market")
                || intent.getScheme().equals("http")
                || intent.getScheme().equals("https")
        )) {
            return intent.getData().getQueryParameter("id");
        }
        return null;
    }

    public void redrawDetails(App app) {
        setTitle(app.getDisplayName());
        new GeneralDetails(this, app).draw();
        new Permissions(this, app).draw();
        new Screenshot(this, app).draw();
        new Review(this, app).draw();
        new AppLists(this, app).draw();
        new BackToPlayStore(this, app).draw();
        new Share(this, app).draw();
        new SystemAppPage(this, app).draw();
        new Video(this, app).draw();
        new Beta(this, app).draw();
        if (null != downloadOrInstallFragment) {
            downloadOrInstallFragment.unregisterReceivers();
        }
        downloadOrInstallFragment = new DownloadOrInstall(this, app);
        redrawButtons();
        new DownloadOptions(this, app).draw();
    }

    static class GetAndRedrawDetailsTask extends DetailsTask implements CloneableTask {

        private DetailsActivity activity;

        public GetAndRedrawDetailsTask(DetailsActivity activity) {
            this.activity = activity;
            setContext(activity);
        }

        @Override
        public CloneableTask clone() {
            GetAndRedrawDetailsTask task = new GetAndRedrawDetailsTask(activity);
            task.setErrorView(errorView);
            task.setPackageName(packageName);
            task.setProgressIndicator(progressIndicator);
            return task;
        }

        @Override
        protected void onPostExecute(App app) {
            super.onPostExecute(app);
            if (app != null) {
                DetailsActivity.app = app;
                activity.redrawDetails(app);
            }
        }
    }
}
