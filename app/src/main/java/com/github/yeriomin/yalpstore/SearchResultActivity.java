package com.github.yeriomin.yalpstore;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Spinner;

import java.util.regex.Pattern;

public class SearchResultActivity extends EndlessScrollActivity {

    static private final String PUBLISHER_PREFIX = "pub:";

    private String query;
    private String categoryId = CategoryManager.TOP;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        String newQuery = getQuery(intent);
        Log.i(getClass().getName(), "Searching: " + newQuery);
        if (null != newQuery && !newQuery.equals(this.query)) {
            clearApps();
            this.categoryId = CategoryManager.TOP;
            this.query = newQuery;
            setTitle(getSearchTitle());
            if (looksLikeAPackageId(query)) {
                Log.i(getClass().getName(), query + " looks like a package id");
                checkPackageId(query);
            } else {
                loadApps();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new CategoryManager(this).fill((Spinner) findViewById(R.id.filter));
    }

    public void setCategoryId(String categoryId) {
        if (!categoryId.equals(this.categoryId)) {
            this.categoryId = categoryId;
            clearApps();
            loadApps();
        }
    }

    public void loadApps() {
        SearchTask task = getTask();
        task.setTaskClone(getTask());
        task.execute(query, categoryId);
    }

    private SearchTask getTask() {
        SearchTask task = new SearchTask() {

            @Override
            protected void onPostExecute(Throwable e) {
                super.onPostExecute(e);
                if (e == null) {
                    addApps(apps);
                } else {
                    clearApps();
                }
            }
        };
        task.setCategoryManager(new CategoryManager(this));
        return (SearchTask) prepareTask(task);
    }

    private String getSearchTitle() {
        if (query.startsWith(PUBLISHER_PREFIX)) {
            return getString(R.string.activity_title_search_publisher, query.substring(PUBLISHER_PREFIX.length()));
        }
        return getString(R.string.activity_title_search, query);
    }

    private String getQuery(Intent intent) {
        if (intent.getScheme() != null
            && (intent.getScheme().equals("market")
            || intent.getScheme().equals("http")
            || intent.getScheme().equals("https"))
        ) {
            return intent.getData().getQueryParameter("q");
        }
        if (intent.getAction().equals(Intent.ACTION_SEARCH)) {
            return intent.getStringExtra(SearchManager.QUERY);
        } else if (intent.getAction().equals(Intent.ACTION_VIEW)) {
            return intent.getDataString();
        }
        return null;
    }

    private boolean looksLikeAPackageId(String query) {
        String pattern = "([\\p{L}_$][\\p{L}\\p{N}_$]*\\.)+[\\p{L}_$][\\p{L}\\p{N}_$]*";
        Pattern r = Pattern.compile(pattern);
        return r.matcher(query).matches();
    }

    private void checkPackageId(String packageId) {
        DetailsTask task = new DetailsTask() {
            @Override
            protected void onPostExecute(Throwable result) {
                super.onPostExecute(result);
                if (null != app) {
                    showPackageIdDialog(app.getPackageName());
                }
            }
        };
        task.setContext(this);
        task.setPackageName(packageId);
        task.execute();
    }

    private AlertDialog showPackageIdDialog(final String packageId) {
        return new AlertDialog.Builder(this)
            .setMessage(R.string.dialog_message_package_id)
            .setTitle(R.string.dialog_title_package_id)
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    DetailsActivity.start(SearchResultActivity.this, packageId);
                    dialogInterface.dismiss();
                    finish();
                }
            })
            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    loadApps();
                }
            })
            .show();
    }
}
