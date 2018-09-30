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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.github.yeriomin.playstoreapi.GooglePlayAPI;
import com.github.yeriomin.playstoreapi.GooglePlayException;
import com.github.yeriomin.playstoreapi.ReviewResponse;
import com.github.yeriomin.yalpstore.download.DetailsProgressListener;
import com.github.yeriomin.yalpstore.download.DownloadManager;
import com.github.yeriomin.yalpstore.fragment.ButtonCancel;
import com.github.yeriomin.yalpstore.fragment.ButtonDownload;
import com.github.yeriomin.yalpstore.fragment.ButtonInstall;
import com.github.yeriomin.yalpstore.fragment.ButtonRun;
import com.github.yeriomin.yalpstore.fragment.ButtonUninstall;
import com.github.yeriomin.yalpstore.fragment.DownloadMenu;
import com.github.yeriomin.yalpstore.fragment.details.AllFragments;
import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.model.ReviewBuilder;
import com.github.yeriomin.yalpstore.task.playstore.CloneableTask;
import com.github.yeriomin.yalpstore.task.playstore.DetailsTask;

import java.io.IOException;

public class DetailsActivity extends YalpStoreActivity {

    static private final String INTENT_PACKAGE_NAME = "INTENT_PACKAGE_NAME";

    static public App app;

    protected DetailsInstallReceiver installReceiver;

    static public Intent getDetailsIntent(Context context, String packageName) {
        Intent intent = new Intent(context, DetailsActivity.class);
        intent.putExtra(DetailsActivity.INTENT_PACKAGE_NAME, packageName);
        if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
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
        super.onPause();
        unregisterInstallReceiver();
    }

    @Override
    protected void onResume() {
        redrawButtons();
        super.onResume();
    }

    protected void unregisterInstallReceiver() {
        unregisterReceiver(installReceiver);
        installReceiver = null;
    }

    public void redrawButtons() {
        unregisterInstallReceiver();
        if (null == app) {
            return;
        }
        installReceiver = new DetailsInstallReceiver(this, app.getPackageName());
        new ButtonUninstall(this, app).draw();
        new ButtonDownload(this, app).draw();
        new ButtonCancel(this, app).draw();
        new ButtonInstall(this, app).draw();
        new ButtonRun(this, app).draw();
        if (DownloadManager.isRunning(app.getPackageName())) {
            DownloadManager.addProgressListener(app.getPackageName(), new DetailsProgressListener(this));
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
        if (YalpStorePermissionManager.isGranted(requestCode, permissions, grantResults) && null != app) {
            redrawButtons();
            new ButtonDownload(this, app).download();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        if (null != app) {
            new DownloadMenu(this, app).onCreateOptionsMenu(menu);
        }
        return result;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        new DownloadMenu(this, app).inflate(menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return new DownloadMenu(this, app).onContextItemSelected(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return new DownloadMenu(this, app).onContextItemSelected(item) || super.onOptionsItemSelected(item);
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
        new AllFragments(this, app).draw();
        unregisterInstallReceiver();
        redrawButtons();
        new DownloadMenu(this, app).draw();
    }

    static class GetAndRedrawDetailsTask extends DetailsTask implements CloneableTask {

        public GetAndRedrawDetailsTask(DetailsActivity activity) {
            setContext(activity);
        }

        @Override
        public CloneableTask clone() {
            GetAndRedrawDetailsTask task = new GetAndRedrawDetailsTask((DetailsActivity) context);
            task.setErrorView(errorView);
            task.setPackageName(packageName);
            task.setProgressIndicator(progressIndicator);
            return task;
        }

        @Override
        protected App getResult(GooglePlayAPI api, String... arguments) throws IOException {
            App app = super.getResult(api, arguments);
            if (null == app.getUserReview() && !YalpStoreApplication.user.appProvidedEmail()) {
                ReviewResponse reviewResponse = api.getReview(app.getPackageName());
                if (reviewResponse.hasGetResponse() && reviewResponse.getGetResponse().getReviewCount() == 1) {
                    app.setUserReview(ReviewBuilder.build(reviewResponse.getGetResponse().getReview(0)));
                }
            }
            return app;
        }

        @Override
        protected void processIOException(IOException e) {
        }

        @Override
        protected void onPostExecute(App app) {
            super.onPostExecute(app);
            if (app != null) {
                DetailsActivity.app = app;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    ((DetailsActivity) context).invalidateOptionsMenu();
                }
                ((DetailsActivity) context).redrawDetails(app);
            }
            Throwable e = getException();
            if (null != e && e instanceof GooglePlayException && ((GooglePlayException) e).getCode() == 404) {
                TextView availability = ((DetailsActivity) context).findViewById(R.id.availability);
                availability.setVisibility(View.VISIBLE);
                availability.setText(R.string.details_not_available_on_play_store);
            }
        }
    }
}
