package com.github.yeriomin.yalpstore;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.yeriomin.yalpstore.model.App;

import java.util.ArrayList;
import java.util.List;

public class DetailsActivity extends Activity {

    private static final int PERMISSIONS_REQUEST_CODE = 828;

    static final String INTENT_PACKAGE_NAME = "INTENT_PACKAGE_NAME";

    private GoogleApiAsyncTask task;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, PreferenceActivity.class));
                break;
            case R.id.action_logout:
                new AlertDialog.Builder(this)
                    .setMessage(R.string.dialog_message_logout)
                    .setTitle(R.string.dialog_title_logout)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            new PlayStoreApiWrapper(getApplicationContext()).logout();
                            dialogInterface.dismiss();
                            finish();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .show();
                break;
            case R.id.action_search:
                onSearchRequested();
                break;
            case R.id.action_updates:
                startActivity(new Intent(this, UpdatableAppsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        final String packageName = getIntentPackageName(intent);
        if (packageName == null || packageName.isEmpty()) {
            Toast.makeText(this, "No package name provided", Toast.LENGTH_LONG).show();
            finishActivity(0);
            return;
        }

        GoogleApiAsyncTask task = new GoogleApiAsyncTask() {

            private App app;

            @Override
            protected Throwable doInBackground(Void... params) {
                PlayStoreApiWrapper wrapper = new PlayStoreApiWrapper(getApplicationContext());
                try {
                    this.app = wrapper.getDetails(packageName);
                } catch (Throwable e) {
                    return e;
                }
                Drawable icon;
                try {
                    ApplicationInfo installedApp = getPackageManager().getApplicationInfo(packageName, 0);
                    icon = getPackageManager().getApplicationIcon(installedApp);
                    this.app.setInstalled(true);
                } catch (PackageManager.NameNotFoundException e) {
                    BitmapManager manager = new BitmapManager(getApplicationContext());
                    icon = null == app.getIconUrl()
                            ? this.context.getResources().getDrawable(android.R.drawable.sym_def_app_icon)
                            : new BitmapDrawable(manager.getBitmap(app.getIconUrl()))
                    ;
                }
                this.app.setIcon(icon);
                return null;
            }

            @Override
            protected void onPostExecute(Throwable e) {
                super.onPostExecute(e);
                if (this.app != null) {
                    drawDetails(this.app);
                } else {
                    Log.e(getClass().getName(), "Could not get requested app");
                    finishActivity(0);
                }
            }
        };
        task.setContext(this);
        task.prepareDialog(
            getString(R.string.dialog_message_loading_app_details),
            getString(R.string.dialog_title_loading_app_details)
        );
        task.execute();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onNewIntent(getIntent());
    }

    private String getIntentPackageName(Intent intent) {
        if (intent.hasExtra(INTENT_PACKAGE_NAME)) {
            return intent.getStringExtra(INTENT_PACKAGE_NAME);
        } else if (intent.getScheme() != null && intent.getScheme().equals("market")) {
            return intent.getData().getQueryParameter("id");
        }
        return null;
    }

    private void drawDetails(final App app) {
        setTitle(app.getDisplayName());
        setContentView(R.layout.details_activity_layout);

        ((ImageView) findViewById(R.id.icon)).setImageDrawable(app.getIcon());

        setText(R.id.displayName, app.getDisplayName());
        setText(R.id.packageName, app.getPackageName());
        setText(R.id.installs, R.string.details_installs, app.getInstalls());
        setText(R.id.rating, R.string.details_rating, app.getRating());
        setText(R.id.updated, R.string.details_updated, app.getUpdated());
        setText(R.id.size, R.string.details_size, Formatter.formatShortFileSize(this, app.getSize()));
        setText(R.id.description, Html.fromHtml(app.getDescription()).toString());
        setText(R.id.developerName, R.string.details_developer, app.getDeveloper().getName());
        setText(R.id.developerEmail, app.getDeveloper().getEmail());
        setText(R.id.developerWebsite, app.getDeveloper().getWebsite());
        String changes = app.getChanges();
        if (null != changes && !changes.isEmpty()) {
            setText(R.id.changes, Html.fromHtml(changes).toString());
            findViewById(R.id.changes).setVisibility(View.VISIBLE);
            findViewById(R.id.changes_title).setVisibility(View.VISIBLE);
        }
        String versionName = app.getVersionName();
        if (null != versionName && !versionName.isEmpty()) {
            setText(R.id.versionString, R.string.details_versionName, versionName);
            findViewById(R.id.versionString).setVisibility(View.VISIBLE);
            if (app.isInstalled()) {
                try {
                    PackageInfo info = getPackageManager().getPackageInfo(app.getPackageName(), 0);
                    if (info.versionCode != app.getVersionCode()) {
                        setText(R.id.versionString, R.string.details_versionName_updatable, info.versionName, versionName);
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    // We've checked for that already
                }
            }
        }

        PackageManager pm = getPackageManager();
        List<String> localizedPermissions = new ArrayList<>();
        for (String permissionName: app.getPermissions()) {
            try {
                localizedPermissions.add(pm.getPermissionInfo(permissionName, 0).loadLabel(pm).toString());
            } catch (PackageManager.NameNotFoundException e) {
                Log.w(getClass().getName(), "No human-readable name found for permission " + permissionName);
            }
        }
        setText(R.id.permissions, TextUtils.join("\n", localizedPermissions));

        Button downloadButton = (Button) findViewById(R.id.download);
        if (!app.isFree()) {
            downloadButton.setText(getString(R.string.details_download_nonfree));
            downloadButton.setEnabled(false);
        } else if (app.getVersionCode() == 0) {
            downloadButton.setText(getString(R.string.details_download_impossible));
            downloadButton.setEnabled(false);
        } else {
            downloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    task = new GoogleApiAsyncTask() {
                        @Override
                        protected Throwable doInBackground(Void... params) {
                            PlayStoreApiWrapper wrapper = new PlayStoreApiWrapper(DetailsActivity.this);
                            try {
                                wrapper.download(app);
                            } catch (Throwable e) {
                                return e;
                            }
                            return null;
                        }
                    };
                    task.setContext(v.getContext());
                    task.prepareDialog(
                        getString(R.string.dialog_message_purchasing_app),
                        getString(R.string.dialog_title_purchasing_app)
                    );
                    if (checkPermission()) {
                        task.execute();
                    } else {
                        requestPermission();
                    }
                }
            });
        }
    }

    private void setText(int viewId, String text) {
        ((TextView) findViewById(viewId)).setText(text);
    }

    private void setText(int viewId, int stringId, Object... text) {
        setText(viewId, getString(stringId, text));
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
             return this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                 == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE
            && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            task.execute();
        }
    }

}
