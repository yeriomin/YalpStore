/*
 * Yalp Store
 * Copyright (C) 2018 Sergey Yeriomin <yeriomin@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.yeriomin.yalpstore.fragment.details.AppLists;
import com.github.yeriomin.yalpstore.fragment.details.BackToPlayStore;
import com.github.yeriomin.yalpstore.fragment.details.Background;
import com.github.yeriomin.yalpstore.fragment.details.Beta;
import com.github.yeriomin.yalpstore.fragment.details.DownloadOptions;
import com.github.yeriomin.yalpstore.fragment.details.DownloadOrInstall;
import com.github.yeriomin.yalpstore.fragment.details.GeneralDetails;
import com.github.yeriomin.yalpstore.fragment.details.Permissions;
import com.github.yeriomin.yalpstore.fragment.details.Review;
import com.github.yeriomin.yalpstore.fragment.details.Screenshot;
import com.github.yeriomin.yalpstore.fragment.details.Share;
import com.github.yeriomin.yalpstore.fragment.details.SystemAppPage;
import com.github.yeriomin.yalpstore.fragment.details.Video;
import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.task.playstore.CloneableTask;
import com.github.yeriomin.yalpstore.task.playstore.DetailsTask;

public class DetailsActivity extends YalpStoreActivity {

    static private final String INTENT_PACKAGE_NAME = "INTENT_PACKAGE_NAME";

    static public App app;

    protected DownloadOrInstall downloadOrInstallFragment;

    static public Intent getDetailsIntent(Context context, String packageName) {
        Intent intent = new Intent(context, DetailsActivity.class);
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
        super.onCreate(savedInstanceState);
        setContentViewNoWrapper(R.layout.details_activity_layout);
        onNewIntent(getIntent());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (YalpStorePermissionManager.isGranted(requestCode, permissions, grantResults)) {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        if (null != app) {
            new DownloadOptions(this, app).onCreateOptionsMenu(menu);
        }
        return result;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return new DownloadOptions(this, app).onContextItemSelected(item) || super.onOptionsItemSelected(item);
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
        new Background(this, app).draw();
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
