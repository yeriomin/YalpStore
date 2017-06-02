package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.yeriomin.yalpstore.fragment.details.AppLists;
import com.github.yeriomin.yalpstore.fragment.details.BackToPlayStore;
import com.github.yeriomin.yalpstore.fragment.details.DownloadOptions;
import com.github.yeriomin.yalpstore.fragment.details.DownloadOrInstall;
import com.github.yeriomin.yalpstore.fragment.details.GeneralDetails;
import com.github.yeriomin.yalpstore.fragment.details.IgnoreOption;
import com.github.yeriomin.yalpstore.fragment.details.Review;
import com.github.yeriomin.yalpstore.fragment.details.Screenshot;
import com.github.yeriomin.yalpstore.fragment.details.Share;
import com.github.yeriomin.yalpstore.fragment.details.SystemAppPage;
import com.github.yeriomin.yalpstore.model.App;

public class DetailsActivity extends YalpStoreActivity {

    static public final int PERMISSIONS_REQUEST_CODE = 828;

    static private final String INTENT_PACKAGE_NAME = "INTENT_PACKAGE_NAME";

    protected DownloadOrInstall downloadOrInstallFragment;
    private IgnoreOption ignoreOptionFragment;
    private DownloadOptions downloadOptionsFragment;

    static public void start(Context context, String packageName) {
        Intent intent = new Intent(context, DetailsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(DetailsActivity.INTENT_PACKAGE_NAME, packageName);
        context.startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        if (null != ignoreOptionFragment) {
            ignoreOptionFragment.setMenu(menu);
            ignoreOptionFragment.draw();
        }
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_ignore) {
            ignoreOptionFragment.toggleBlackWhiteList();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        final String packageName = getIntentPackageName(intent);
        if (TextUtils.isEmpty(packageName)) {
            Log.e(this.getClass().getName(), "No package name provided");
            finishActivity(0);
            return;
        }
        Log.i(getClass().getName(), "Getting info about " + packageName);
        ignoreOptionFragment = new IgnoreOption(this, new App());

        if (null != DetailsDependentActivity.app) {
            drawDetails(DetailsDependentActivity.app);
        }

        DetailsTask task = getDetailsTask(packageName);
        task.setTaskClone(getDetailsTask(packageName));
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
        if (null != downloadOrInstallFragment) {
            downloadOrInstallFragment.unregisterReceivers();
            downloadOrInstallFragment.registerReceivers();
            downloadOrInstallFragment.draw();
        }
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onNewIntent(getIntent());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE
            && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            downloadOrInstallFragment.download();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        downloadOptionsFragment.inflate(menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return downloadOptionsFragment.onContextItemSelected(item);
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

    public void drawDetails(App app) {
        setTitle(app.getDisplayName());
        setContentView(R.layout.details_activity_layout);
        new GeneralDetails(this, app).draw();
        new Screenshot(this, app).draw();
        new Review(this, app).draw();
        new AppLists(this, app).draw();
        new BackToPlayStore(this, app).draw();
        new Share(this, app).draw();
        new SystemAppPage(this, app).draw();
        ignoreOptionFragment.setApp(app);
        ignoreOptionFragment.draw();
        if (null != downloadOrInstallFragment) {
            downloadOrInstallFragment.unregisterReceivers();
        }
        downloadOrInstallFragment = new DownloadOrInstall(this, app);
        downloadOrInstallFragment.registerReceivers();
        downloadOrInstallFragment.draw();
        downloadOptionsFragment = new DownloadOptions(this, app);
        downloadOptionsFragment.draw();
    }

    private DetailsTask getDetailsTask(String packageName) {
        DetailsTask task = new DetailsTask() {

            @Override
            protected void onPostExecute(Throwable e) {
                super.onPostExecute(e);
                if (this.app != null) {
                    DetailsDependentActivity.app = this.app;
                    drawDetails(this.app);
                }
            }
        };
        task.setPackageName(packageName);
        task.setContext(this);
        task.setProgressIndicator(findViewById(R.id.progress));
        return task;
    }
}
