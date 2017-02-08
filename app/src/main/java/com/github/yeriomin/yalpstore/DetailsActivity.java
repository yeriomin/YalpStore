package com.github.yeriomin.yalpstore;

import android.Manifest;
import android.app.DownloadManager;
import android.app.SearchManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.yeriomin.yalpstore.model.App;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DetailsActivity extends YalpStoreActivity {

    static private final int PERMISSIONS_REQUEST_CODE = 828;

    static public final String INTENT_PACKAGE_NAME = "INTENT_PACKAGE_NAME";

    private Menu menu;
    private App app;
    private PurchaseTask purchaseTask;
    private DetailsDownloadReceiver receiver;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        addBlackWhiteListOption();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_ignore) {
            toggleBlackWhiteList();
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
        Log.i(this.getClass().getName(), "Getting info about " + packageName);

        DetailsTask task = new DetailsTask() {

            @Override
            protected void onPostExecute(Throwable e) {
                super.onPostExecute(e);
                if (this.app != null) {
                    DetailsDependentActivity.app = this.app;
                    drawDetails(this.app);
                } else {
                    Log.e(getClass().getName(), "Could not get requested app");
                    finishActivity(0);
                }
            }
        };
        task.setContext(this);
        task.prepareDialog(R.string.dialog_message_loading_app_details, R.string.dialog_title_loading_app_details);
        task.execute(packageName);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        receiver = new DetailsDownloadReceiver();
        receiver.setButton((Button) findViewById(R.id.download));
        registerReceiver(receiver, filter);
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
            File dir = PlayStoreApiWrapper.getApkPath(app).getParentFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }
            if (dir.exists() && dir.isDirectory() && dir.canWrite()) {
                purchaseTask.execute();
            } else {
                Toast.makeText(
                    getApplicationContext(),
                    getString(R.string.error_downloads_directory_not_writable),
                    Toast.LENGTH_LONG
                ).show();
            }
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

    private void drawDetails(final App app) {
        this.app = app;
        setTitle(app.getDisplayName());
        setContentView(R.layout.details_activity_layout);
        addBlackWhiteListOption();
        drawGeneralDetails(app);
        drawDescription(app);
        drawScreenshots(app);
        drawReviews(app);
        drawPermissions(app);
        drawAppListsButtons(app);
        drawDownloadButton(app);
    }

    private void drawGeneralDetails(App app) {
        ((ImageView) findViewById(R.id.icon)).setImageDrawable(app.getIcon());

        setText(R.id.displayName, app.getDisplayName());
        setText(R.id.packageName, app.getPackageName());
        setText(R.id.installs, R.string.details_installs, app.getInstalls());
        setText(R.id.rating, R.string.details_rating, app.getRating().getAverage());
        setText(R.id.updated, R.string.details_updated, app.getUpdated());
        setText(R.id.size, R.string.details_size, Formatter.formatShortFileSize(this, app.getSize()));
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
        String label = getString(R.string.details_versionName, versionName);
        if (app.isInstalled()) {
            try {
                PackageInfo info = getPackageManager().getPackageInfo(app.getPackageName(), 0);
                if (info.versionCode != app.getVersionCode()) {
                    label = getString(R.string.details_versionName_updatable, info.versionName, versionName);
                }
            } catch (PackageManager.NameNotFoundException e) {
                // We've checked for that already
            }
        }
        textView.setText(label);
        textView.setVisibility(View.VISIBLE);
    }

    private void drawDescription(App app) {
        setText(R.id.description, Html.fromHtml(app.getDescription()).toString());
        initExpandableGroup(R.id.description_header, R.id.description_container);
    }

    private void drawScreenshots(final App app) {
        if (app.getScreenshotUrls().size() > 0) {
            findViewById(R.id.screenshots_header).setVisibility(View.VISIBLE);
            Gallery gallery = ((Gallery) findViewById(R.id.screenshots_gallery));
            int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
            gallery.setAdapter(new ImageAdapter(this, app.getScreenshotUrls(), screenWidth));
            gallery.setSpacing(10);
            gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getApplicationContext(), FullscreenImageActivity.class);
                    intent.putExtra(FullscreenImageActivity.INTENT_URL, app.getScreenshotUrls().get(position));
                    startActivity(intent);
                }
            });
            initExpandableGroup(R.id.screenshots_header, R.id.screenshots_container);
        } else {
            findViewById(R.id.screenshots_header).setVisibility(View.GONE);
        }
    }

    private void drawReviews(App app) {
        ReviewManager manager = new ReviewManager(this, app);
        manager.drawReviews();
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

    private void drawAppListsButtons(final App app) {
        findViewById(R.id.apps_by_this_dev).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DetailsActivity.this, SearchResultActivity.class);
                i.setAction(Intent.ACTION_SEARCH);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.putExtra(SearchManager.QUERY, "pub:" + app.getDeveloper().getName());
                startActivity(i);
            }
        });
        findViewById(R.id.similar_apps).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DetailsActivity.this, SimilarAppsActivity.class));
            }
        });
        findViewById(R.id.users_also_installed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DetailsActivity.this, UsersAlsoInstalledActivity.class));
            }
        });
    }

    private void drawDownloadButton(final App app) {
        Button downloadButton = (Button) findViewById(R.id.download);
        if (app.getVersionCode() == 0) {
            downloadButton.setText(getString(R.string.details_download_impossible));
            downloadButton.setEnabled(false);
        } else {
            final boolean exists = PlayStoreApiWrapper.getApkPath(app).exists();
            if (exists) {
                downloadButton.setText(R.string.details_install);
            }
            downloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    purchaseTask = getPurchaseTask(exists);
                    if (checkPermission()) {
                        purchaseTask.execute();
                    } else {
                        requestPermission();
                    }
                }
            });
        }
    }

    private PurchaseTask getPurchaseTask(final boolean apkExists) {
        PurchaseTask purchaseTask = new PurchaseTask() {
            @Override
            protected void onPostExecute(Throwable e) {
                super.onPostExecute(e);
                if (null == e) {
                    receiver.setDownloadId(downloadId);
                    if (!apkExists) {
                        Button button = (Button) findViewById(R.id.download);
                        button.setText(R.string.details_downloading);
                        button.setEnabled(false);
                    }
                }
            }
        };
        purchaseTask.setApp(app);
        purchaseTask.setContext(this);
        purchaseTask.prepareDialog(
            R.string.dialog_message_purchasing_app,
            R.string.dialog_title_purchasing_app
        );
        return purchaseTask;
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

    private void initExpandableGroup(int viewIdHeader, int viewIdContainer) {
        initExpandableGroup(viewIdHeader, viewIdContainer, null);
    }

    private MenuItem getIgnoreMenuItem() {
        if (null != menu) {
            for (int i = 0; i < menu.size(); i++) {
                MenuItem item = menu.getItem(i);
                if (item.getItemId() == R.id.action_ignore) {
                    return item;
                }
            }
        }
        return null;
    }

    private void addBlackWhiteListOption() {
        MenuItem item = getIgnoreMenuItem();
        if (null != item && null != app && app.isInstalled()) {
            item.setVisible(true);
            updateBlackWhiteListItemTitle(item);
        }
    }

    private void toggleBlackWhiteList() {
        MenuItem item = getIgnoreMenuItem();
        if (null != item && null != app && app.isInstalled()) {
            BlackWhiteListManager manager = new BlackWhiteListManager(this);
            if (manager.contains(app.getPackageName())) {
                manager.remove(app.getPackageName());
            } else {
                manager.add(app.getPackageName());
            }
            updateBlackWhiteListItemTitle(item);
        }
    }

    private void updateBlackWhiteListItemTitle(MenuItem item) {
        BlackWhiteListManager manager = new BlackWhiteListManager(this);
        boolean inList = manager.contains(app.getPackageName());
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isBlacklist = prefs.getString(
            PreferenceActivity.PREFERENCE_UPDATE_LIST_WHITE_OR_BLACK,
            PreferenceActivity.LIST_BLACK
        ).equals(PreferenceActivity.LIST_BLACK);
        if (isBlacklist) {
            item.setTitle(getString(inList ? R.string.action_unignore : R.string.action_ignore));
        } else {
            item.setTitle(getString(inList ? R.string.action_unwhitelist : R.string.action_whitelist));
        }
    }

}
