package com.github.yeriomin.yalpstore;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.task.playstore.DetailsTask;
import com.github.yeriomin.yalpstore.task.playstore.SearchTask;

import java.util.regex.Pattern;

public class SearchActivity extends EndlessScrollActivity {

    private String query;
    private String categoryId = CategoryManager.TOP;

    static protected boolean actionIs(Intent intent, String action) {
        return null != intent && null != intent.getAction() && intent.getAction().equals(action);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        String newQuery = getQuery(intent);
        if (actionIs(intent, Intent.ACTION_VIEW) && looksLikeAPackageId(newQuery)) {
            Log.i(getClass().getName(), "Following search suggestion to app page: " + newQuery);
            startActivity(DetailsActivity.getDetailsIntent(this, newQuery));
            finish();
            return;
        }
        Log.i(getClass().getName(), "Searching: " + newQuery);
        if (null != newQuery && !newQuery.equals(this.query)) {
            clearApps();
            this.categoryId = CategoryManager.TOP;
            this.query = newQuery;
            setTitle(getString(R.string.activity_title_search, query));
            if (looksLikeAPackageId(query)) {
                Log.i(getClass().getName(), query + " looks like a package id");
                checkPackageId(query);
            } else {
                loadApps();
            }
        }
    }

    public void setCategoryId(String categoryId) {
        if (!categoryId.equals(this.categoryId)) {
            this.categoryId = categoryId;
            clearApps();
            loadApps();
        }
    }

    @Override
    protected SearchTask getTask() {
        SearchTask task = new SearchTask(iterator);
        task.setQuery(query);
        task.setCategoryId(categoryId);
        return task;
    }

    private String getQuery(Intent intent) {
        if (intent.getScheme() != null
            && (intent.getScheme().equals("market")
                || intent.getScheme().equals("http")
                || intent.getScheme().equals("https")
            )
        ) {
            return intent.getData().getQueryParameter("q");
        }
        if (actionIs(intent, Intent.ACTION_SEARCH)) {
            return intent.getStringExtra(SearchManager.QUERY);
        } else if (actionIs(intent, Intent.ACTION_VIEW)) {
            return intent.getDataString();
        }
        return null;
    }

    private boolean looksLikeAPackageId(String query) {
        if (TextUtils.isEmpty(query)) {
            return false;
        }
        String pattern = "([\\p{L}_$][\\p{L}\\p{N}_$]*\\.)+[\\p{L}_$][\\p{L}\\p{N}_$]*";
        Pattern r = Pattern.compile(pattern);
        return r.matcher(query).matches();
    }

    private void checkPackageId(String packageId) {
        DetailsTask task = new CheckPackageIdTask(this);
        task.setContext(this);
        task.setPackageName(packageId);
        task.execute();
    }

    static private class CheckPackageIdTask extends DetailsTask {

        private SearchActivity activity;

        public CheckPackageIdTask(SearchActivity activity) {
            this.activity = activity;
        }

        @Override
        protected void onPostExecute(App app) {
            super.onPostExecute(app);
            if (null != app) {
                DetailsActivity.app = app;
                showPackageIdDialog(app.getPackageName());
            }
        }

        private AlertDialog showPackageIdDialog(final String packageId) {
            return new AlertDialog.Builder(activity)
                .setMessage(R.string.dialog_message_package_id)
                .setTitle(R.string.dialog_title_package_id)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        activity.startActivity(DetailsActivity.getDetailsIntent(activity, packageId));
                        dialogInterface.dismiss();
                        activity.finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.loadApps();
                    }
                })
                .show()
            ;
        }
    }
}
