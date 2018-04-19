package com.dragons.aurora.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.dragons.aurora.AuroraPermissionManager;
import com.dragons.aurora.model.App;
import com.dragons.aurora.task.playstore.DetailsTask;
import com.dragons.aurora.task.playstore.PurchaseTask;

public class DirectDownloadActivity extends AuroraActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String packageName = getIntentPackageName();
        if (null == packageName) {
            finish();
            return;
        }
        if (!new AuroraPermissionManager(this).checkPermission()) {
            startActivity(DetailsActivity.getDetailsIntent(this, packageName));
            finish();
            return;
        }
        Log.i(getClass().getSimpleName(), "Getting package " + packageName);

        DetailsAndPurchaseTask task = new DetailsAndPurchaseTask();
        task.setPackageName(packageName);
        task.setContext(this);
        task.execute();
        finish();
    }

    private String getIntentPackageName() {
        Intent intent = getIntent();
        if (!intent.hasExtra(Intent.EXTRA_TEXT) || TextUtils.isEmpty(intent.getStringExtra(Intent.EXTRA_TEXT))) {
            Log.w(getClass().getSimpleName(), "Intent does not have " + Intent.EXTRA_TEXT);
            return null;
        }
        try {
            return Uri.parse(intent.getStringExtra(Intent.EXTRA_TEXT)).getQueryParameter("id");
        } catch (UnsupportedOperationException e) {
            Log.w(getClass().getSimpleName(), "Could not parse URI " + intent.getStringExtra(Intent.EXTRA_TEXT) + ": " + e.getMessage());
            return null;
        }
    }

    static class DetailsAndPurchaseTask extends DetailsTask {

        @Override
        protected void onPostExecute(App app) {
            if (success()) {
                getPurchaseTask(app).execute();
            } else {
                context.startActivity(DetailsActivity.getDetailsIntent(context, packageName));
            }
        }

        private PurchaseTask getPurchaseTask(App app) {
            PurchaseTask task = new PurchaseTask();
            task.setApp(app);
            task.setContext(context);
            return task;
        }
    }
}
