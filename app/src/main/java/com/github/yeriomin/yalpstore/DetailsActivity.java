package com.github.yeriomin.yalpstore;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.model.Review;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DetailsActivity extends Activity {

    static private final int PERMISSIONS_REQUEST_CODE = 828;
    static private final int REVIEW_SHOW_COUNT = 3;
    static private final int REVIEW_LOAD_COUNT = 15;

    static final String INTENT_PACKAGE_NAME = "INTENT_PACKAGE_NAME";

    private int reviewShowPage = 0;
    private int reviewLoadPage = 0;
    private boolean allReviewsLoaded;
    private GoogleApiAsyncTask task;
    private List<Review> reviews = new ArrayList<>();
    private Menu menu;
    private App app;
    private long downloadId;
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            if (null == extras) {
                return;
            }
            long id = extras.getLong(DownloadManager.EXTRA_DOWNLOAD_ID);
            if (downloadId != id) {
                return;
            }
            DownloadManager.Query q = new DownloadManager.Query();
            q.setFilterById(id);
            DownloadManager dm = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
            Cursor cursor = dm.query(q);
            if (!cursor.moveToFirst()) {
                return;
            }
            int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            int reason = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON));
            Button button = (Button) findViewById(R.id.download);
            if (status == DownloadManager.STATUS_SUCCESSFUL || reason == DownloadManager.ERROR_FILE_ALREADY_EXISTS) {
                button.setText(R.string.details_install);
            } else {
                button.setText(R.string.details_download);
            }
            button.setEnabled(true);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, PreferenceActivity.class));
                break;
            case R.id.action_ignore:
                ignoreUpdates();
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
        Log.i(this.getClass().getName(), "Getting info about " + packageName);

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
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
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
            task.execute();
        }
    }

    private String getIntentPackageName(Intent intent) {
        if (intent.hasExtra(INTENT_PACKAGE_NAME)) {
            return intent.getStringExtra(INTENT_PACKAGE_NAME);
        } else if (intent.getScheme() != null && (intent.getScheme().equals("market") || intent.getScheme().equals("http") || intent.getScheme().equals("https"))) {
            return intent.getData().getQueryParameter("id");
        }
        return null;
    }

    private void drawDetails(final App app) {
        this.app = app;
        setTitle(app.getDisplayName());
        setContentView(R.layout.details_activity_layout);
        addIgnoreOption();

        ((ImageView) findViewById(R.id.icon)).setImageDrawable(app.getIcon());

        setText(R.id.displayName, app.getDisplayName());
        setText(R.id.packageName, app.getPackageName());
        setText(R.id.installs, R.string.details_installs, app.getInstalls());
        setText(R.id.rating, R.string.details_rating, app.getRating().getAverage());
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

        initExpandableGroup(R.id.description_header, R.id.description_container);

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

        final LinearLayout reviewList = (LinearLayout) findViewById(R.id.reviews_list);
        findViewById(R.id.reviews_previous).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateReviews(v, app.getPackageName());
            }
        });
        findViewById(R.id.reviews_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateReviews(v, app.getPackageName());
            }
        });
        initExpandableGroup(R.id.reviews_header, R.id.reviews_container, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReviews(reviewList, app.getPackageName());
            }
        });
        setText(R.id.average_rating, R.string.details_rating, app.getRating().getAverage());
        setText(R.id.stars5, R.string.details_rating_specific, 5, app.getRating().getFiveStars());
        setText(R.id.stars4, R.string.details_rating_specific, 4, app.getRating().getFourStars());
        setText(R.id.stars3, R.string.details_rating_specific, 3, app.getRating().getThreeStars());
        setText(R.id.stars2, R.string.details_rating_specific, 2, app.getRating().getTwoStars());
        setText(R.id.stars1, R.string.details_rating_specific, 1, app.getRating().getOneStar());

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
                    task = new GoogleApiAsyncTask() {
                        @Override
                        protected Throwable doInBackground(Void... params) {
                            PlayStoreApiWrapper wrapper = new PlayStoreApiWrapper(DetailsActivity.this);
                            try {
                                downloadId = wrapper.download(app);
                            } catch (Throwable e) {
                                return e;
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Throwable e) {
                            super.onPostExecute(e);
                            if (null == e && !exists) {
                                Button button = (Button) findViewById(R.id.download);
                                button.setText(R.string.details_downloading);
                                button.setEnabled(false);
                            } else if (e instanceof NotPurchasedException) {
                                Toast.makeText(getApplicationContext(), getString(R.string.error_not_purchased), Toast.LENGTH_LONG).show();
                            }
                        }
                    };
                    task.setContext(v.getContext());
                    task.prepareDialog(
                        getString(R.string.dialog_message_purchasing_app),
                        getString(R.string.dialog_title_purchasing_app)
                    );
                    File dir = PlayStoreApiWrapper.getApkPath(app).getParentFile();
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    if (dir.exists() && dir.isDirectory() && dir.canWrite()) {
                        if (checkPermission()) {
                            task.execute();
                        } else {
                            requestPermission();
                        }
                    } else {
                        Toast.makeText(
                            getApplicationContext(),
                            getString(R.string.error_downloads_directory_not_writable),
                            Toast.LENGTH_LONG
                        ).show();
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

    private void initExpandableGroup(int viewIdHeader, int viewIdContainer, final View.OnClickListener l) {
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

    private void navigateReviews(View v, String packageName) {
        boolean next = v.getId() == R.id.reviews_next;
        if (next) {
            reviewShowPage++;
        } else {
            reviewShowPage--;
        }
        findViewById(R.id.reviews_previous).setVisibility(
            reviewShowPage > 0
                ? View.VISIBLE
                : View.INVISIBLE
        );
        findViewById(R.id.reviews_next).setVisibility(
            reviews.size() > (reviewShowPage * REVIEW_SHOW_COUNT)
                ? View.VISIBLE
                : View.INVISIBLE
        );
        showReviews((LinearLayout) findViewById(R.id.reviews_list), packageName);
    }

    private void showReviews(LinearLayout list, String packageName) {
        int offset = REVIEW_SHOW_COUNT * reviewShowPage;
        if (reviews.size() > offset) {
            list.removeAllViews();
            for (int i = offset; i < Math.min(REVIEW_SHOW_COUNT + offset, reviews.size()); i++) {
                addReview(reviews.get(i), list);
            }
        }
        if (!allReviewsLoaded && reviews.size() < REVIEW_SHOW_COUNT + offset) {
            loadMoreReviews(list, packageName);
        }
    }

    private void addReview(Review review, ViewGroup parent) {
        LinearLayout reviewLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.review_list_item, null, false);
        String title = getString(R.string.details_rating, (double) review.getRating());
        if (null != review.getTitle() && !review.getTitle().isEmpty()) {
            title += " " + review.getTitle();
        }
        ((TextView) reviewLayout.findViewById(R.id.author)).setText(review.getUserName());
        ((TextView) reviewLayout.findViewById(R.id.title)).setText(title);
        ((TextView) reviewLayout.findViewById(R.id.comment)).setText(review.getComment());
        parent.addView(reviewLayout);
        ImageDownloadTask task = new ImageDownloadTask();
        task.setView((ImageView) reviewLayout.findViewById(R.id.avatar));
        task.execute((String) review.getUserPhotoUrl());
    }

    private void loadMoreReviews(final LinearLayout list, final String packageName) {
        GoogleApiAsyncTask task = new GoogleApiAsyncTask() {
            @Override
            protected Throwable doInBackground(Void... params) {
                PlayStoreApiWrapper wrapper = new PlayStoreApiWrapper(DetailsActivity.this);
                try {
                    if (reviews.addAll(wrapper.getReviews(packageName, REVIEW_LOAD_COUNT * reviewLoadPage, REVIEW_LOAD_COUNT))) {
                        reviewLoadPage++;
                    } else {
                        allReviewsLoaded = true;
                    }
                } catch (Throwable e) {
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Throwable e) {
                super.onPostExecute(e);
                if (e == null) {
                    showReviews(list, packageName);
                    findViewById(R.id.reviews_next).setVisibility(
                        reviews.size() > (reviewShowPage * REVIEW_SHOW_COUNT)
                            ? View.VISIBLE
                            : View.INVISIBLE
                    );
                } else {
                    Log.e(DetailsActivity.class.getName(), "Could not get reviews: " + e.getMessage());
                }
            }
        };

        task.setContext(this);
        task.prepareDialog(
            getString(R.string.dialog_message_reviews),
            getString(R.string.dialog_title_reviews)
        );
        task.execute();
    }

    private MenuItem getIgnoreMenuItem() {
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item.getItemId() == R.id.action_ignore) {
                return item;
            }
        }
        return null;
    }

    private void addIgnoreOption() {
        MenuItem item = getIgnoreMenuItem();
        if (null != item) {
            item.setVisible(true);
            IgnoredAppsManager manager = new IgnoredAppsManager(this);
            if (null != app && app.isInstalled() && manager.contains(app.getPackageName())) {
                item.setTitle(getString(R.string.action_unignore));
            }
        }
    }

    private void ignoreUpdates() {
        if (null != app) {
            MenuItem item = getIgnoreMenuItem();
            if (null != item) {
                IgnoredAppsManager manager = new IgnoredAppsManager(this);
                if (manager.contains(app.getPackageName())) {
                    manager.remove(app.getPackageName());
                    item.setTitle(getString(R.string.action_ignore));
                } else {
                    manager.add(app.getPackageName());
                    item.setTitle(getString(R.string.action_unignore));
                }
            }
        }
    }

    class ImageAdapter extends BaseAdapter {

        private Context context;
        private List<String> screenshotUrls;
        private int screenWidth;

        public ImageAdapter(Context context, List<String> screenshotUrls, int screenWidth) {
            this.context = context;
            this.screenshotUrls = screenshotUrls;
            this.screenWidth = screenWidth;
        }

        @Override
        public int getCount() {
            return screenshotUrls.size();
        }

        @Override
        public Object getItem(int position) {
            return screenshotUrls.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageDownloadTask task = new ImageDownloadTask() {

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    Bitmap bitmap = ((BitmapDrawable) this.view.getDrawable()).getBitmap();
                    int w = Math.min(screenWidth, bitmap.getWidth());
                    int h = Math.min(screenWidth, bitmap.getHeight());
                    this.view.setLayoutParams(new Gallery.LayoutParams(w, h));
                    this.view.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    if (this.view.getParent() instanceof Gallery) {
                        Gallery gallery = (Gallery) this.view.getParent();
                        gallery.setMinimumHeight(Math.max(gallery.getMeasuredHeight(), h));
                    }
                }
            };
            ImageView imageView = new ImageView(context);
            task.setFullSize(true);
            task.setView(imageView);
            task.execute((String) getItem(position));
            return imageView;
        }
    }
}
