package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.model.App;

import java.util.ArrayList;
import java.util.List;

public class DetailsActivity extends YalpStoreActivity {

    static public final int PERMISSIONS_REQUEST_CODE = 828;

    static private final String INTENT_PACKAGE_NAME = "INTENT_PACKAGE_NAME";

    private DownloadOrInstallManager downloadOrInstallManager;
    private IgnoreOptionManager ignoreOptionManager;

    static public void start(Context context, String packageName) {
        Intent intent = new Intent(context, DetailsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(DetailsActivity.INTENT_PACKAGE_NAME, packageName);
        context.startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        if (null != ignoreOptionManager) {
            ignoreOptionManager.setMenu(menu);
            ignoreOptionManager.draw();
        }
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_ignore) {
            ignoreOptionManager.toggleBlackWhiteList();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        final String packageName = getIntentPackageName(intent);
        if (packageName == null || packageName.isEmpty()) {
            Log.e(this.getClass().getName(), "No package name provided");
            finishActivity(0);
            return;
        }
        Log.i(this.getClass().getName(), "Getting info about " + packageName);

        DetailsTask task = getDetailsTask(packageName);
        task.setTaskClone(getDetailsTask(packageName));
        task.execute();
        ignoreOptionManager = new IgnoreOptionManager(this, new App());
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
        task.prepareDialog(R.string.dialog_message_loading_app_details, R.string.dialog_title_loading_app_details);
        return task;
    }

    @Override
    protected void onPause() {
        if (null != downloadOrInstallManager) {
            downloadOrInstallManager.unregisterReceiver();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (null != downloadOrInstallManager) {
            downloadOrInstallManager.registerReceiver();
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
            && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            downloadOrInstallManager.downloadOrInstall();
        }
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

    private void drawDetails(App app) {
        setTitle(app.getDisplayName());
        setContentView(R.layout.details_activity_layout);
        drawGeneralDetails(app);
        drawDescription(app);
        new ScreenshotManager(this, app).draw();
        new ReviewManager(this, app).draw();
        drawPermissions(app);
        new AppListsManager(this, app).draw();
        new BackToPlayStoreManager(this, app).draw();
        ignoreOptionManager.setApp(app);
        ignoreOptionManager.draw();
        downloadOrInstallManager = new DownloadOrInstallManager(this, app);
        downloadOrInstallManager.registerReceiver();
        downloadOrInstallManager.draw();
    }

    private void drawGeneralDetails(App app) {
        ((ImageView) findViewById(R.id.icon)).setImageDrawable(app.getIcon());

        setText(R.id.displayName, app.getDisplayName());
        setText(R.id.packageName, app.getPackageName());
        setText(R.id.installs, R.string.details_installs, app.getInstalls());
        setText(R.id.rating, R.string.details_rating, app.getRating().getAverage());
        setText(R.id.updated, R.string.details_updated, app.getUpdated());
        setText(R.id.size, R.string.details_size, Formatter.formatShortFileSize(this, app.getSize()));
        setText(R.id.category, R.string.details_category, new CategoryManager(this).getCategoryName(app.getCategoryId()));
        setText(R.id.developerName, R.string.details_developer, app.getDeveloper().getName());
        setText(R.id.developerEmail, app.getDeveloper().getEmail());
        setText(R.id.developerWebsite, app.getDeveloper().getWebsite());
        drawChanges(app);
        drawVersion((TextView) findViewById(R.id.versionString), app);
        if (app.getVersionCode() == 0) {
            findViewById(R.id.updated).setVisibility(View.GONE);
            findViewById(R.id.size).setVisibility(View.GONE);
        }
    }

    private void drawChanges(App app) {
        String changes = app.getChanges();
        if (null != changes && !changes.isEmpty()) {
            setText(R.id.changes, Html.fromHtml(changes).toString());
            findViewById(R.id.changes).setVisibility(View.VISIBLE);
            findViewById(R.id.changes_title).setVisibility(View.VISIBLE);
        }
    }

    private void drawVersion(TextView textView, App app) {
        String versionName = app.getVersionName();
        if (null == versionName || versionName.isEmpty()) {
            return;
        }
        textView.setText(getString(R.string.details_versionName, versionName));
        textView.setVisibility(View.VISIBLE);
        if (!app.isInstalled()) {
            return;
        }
        try {
            PackageInfo info = getPackageManager().getPackageInfo(app.getPackageName(), 0);
            if (info.versionCode != app.getVersionCode()) {
                textView.setText(getString(R.string.details_versionName_updatable, info.versionName, versionName));
            }
        } catch (PackageManager.NameNotFoundException e) {
            // We've checked for that already
        }
    }

    private void drawDescription(App app) {
        setText(R.id.description, Html.fromHtml(app.getDescription()).toString());
        initExpandableGroup(R.id.description_header, R.id.description_container);
    }

    private void drawPermissions(App app) {
        initExpandableGroup(R.id.permissions_header, R.id.permissions_container);
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
    }

    private void setText(int viewId, String text) {
        ((TextView) findViewById(viewId)).setText(text);
    }

    private void setText(int viewId, int stringId, Object... text) {
        setText(viewId, getString(stringId, text));
    }

    public void initExpandableGroup(int viewIdHeader, int viewIdContainer, final View.OnClickListener l) {
        TextView viewHeader = (TextView) findViewById(viewIdHeader);
        final LinearLayout viewContainer = (LinearLayout) findViewById(viewIdContainer);
        viewHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isExpanded = viewContainer.getVisibility() == View.VISIBLE;
                if (isExpanded) {
                    viewContainer.setVisibility(View.GONE);
                    ((TextView) v).setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_expand_more, 0, 0, 0);
                } else {
                    if (null != l) {
                        l.onClick(v);
                    }
                    viewContainer.setVisibility(View.VISIBLE);
                    ((TextView) v).setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_expand_less, 0, 0, 0);
                }
            }
        });
    }

    public void initExpandableGroup(int viewIdHeader, int viewIdContainer) {
        initExpandableGroup(viewIdHeader, viewIdContainer, null);
    }
}
