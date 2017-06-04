package com.github.yeriomin.yalpstore;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.github.yeriomin.yalpstore.model.App;

public class DirectDownloadActivity extends YalpStoreActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String packageName = getIntentPackageName();
        if (null == packageName) {
            finish();
            return;
        }
        if (!checkPermission()) {
            DetailsActivity.start(this, packageName);
            finish();
            return;
        }
        Log.i(getClass().getName(), "Getting package " + packageName);
        DetailsTask task = getDetailsTask(packageName);
        task.setTaskClone(getDetailsTask(packageName));
        task.execute();
        finish();
    }

    private String getIntentPackageName() {
        Intent intent = getIntent();
        if (!intent.hasExtra(Intent.EXTRA_TEXT)) {
            Log.w(getClass().getName(), "Intent does not have " + Intent.EXTRA_TEXT);
            return null;
        }
        try {
            return Uri.parse(intent.getStringExtra(Intent.EXTRA_TEXT)).getQueryParameter("id");
        } catch (UnsupportedOperationException e) {
            Log.w(getClass().getName(), "Could not parse URI " + intent.getStringExtra(Intent.EXTRA_TEXT) + ": " + e.getMessage());
            return null;
        }
    }

    private DetailsTask getDetailsTask(final String packageName) {
        DetailsTask task = new DetailsTask() {
            @Override
            protected void onPostExecute(Throwable result) {
                Throwable e = result;
                if (result instanceof RuntimeException && null != result.getCause()) {
                    e = result.getCause();
                }
                if (null == e) {
                    getPurchaseTask(app).execute();
                } else {
                    DetailsActivity.start(context, packageName);
                }
            }
        };
        task.setPackageName(packageName);
        task.setContext(this);
        return task;
    }

    private PurchaseTask getPurchaseTask(App app) {
        PurchaseTask task = new PurchaseTask();
        task.setApp(app);
        task.setContext(this);
        return task;
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }
}
