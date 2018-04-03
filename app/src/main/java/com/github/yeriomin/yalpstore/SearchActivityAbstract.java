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

import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;

import com.github.yeriomin.yalpstore.fragment.FilterMenu;
import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.task.playstore.DetailsTask;
import com.github.yeriomin.yalpstore.task.playstore.SearchTask;
import com.github.yeriomin.yalpstore.view.DialogWrapper;
import com.github.yeriomin.yalpstore.view.DialogWrapperAbstract;

import java.util.regex.Pattern;

abstract public class SearchActivityAbstract extends EndlessScrollActivity {

    public static final String PUB_PREFIX = "pub:";

    protected String query;

    static protected boolean actionIs(Intent intent, String action) {
        return null != intent && null != intent.getAction() && intent.getAction().equals(action);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        String newQuery = getQuery(intent);
        if (actionIs(intent, Intent.ACTION_VIEW) && looksLikeAPackageId(newQuery)) {
            Log.i(getClass().getSimpleName(), "Following search suggestion to app page: " + newQuery);
            startActivity(DetailsActivity.getDetailsIntent(this, newQuery));
            finish();
            return;
        }
        Log.i(getClass().getSimpleName(), "Searching: " + newQuery);
        if (null != newQuery && !newQuery.equals(this.query)) {
            clearApps();
            this.query = newQuery;
            setTitle(getTitleString());
            if (looksLikeAPackageId(query)) {
                Log.i(getClass().getSimpleName(), query + " looks like a package id");
                checkPackageId(query);
            } else {
                loadApps();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.findItem(R.id.filter_category).setVisible(true);
        return result;
    }

    @Override
    protected SearchTask getTask() {
        SearchTask task = new SearchTask(iterator);
        task.setQuery(query);
        task.setFilter(new FilterMenu(this).getFilterPreferences());
        return task;
    }

    private String getTitleString() {
        return query.startsWith(PUB_PREFIX)
            ? getString(R.string.apps_by, query.substring(PUB_PREFIX.length()))
            : getString(R.string.activity_title_search, query)
        ;
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

        private SearchActivityAbstract activity;

        public CheckPackageIdTask(SearchActivityAbstract activity) {
            this.activity = activity;
        }

        @Override
        protected void onPostExecute(App app) {
            super.onPostExecute(app);
            if (null != app && ContextUtil.isAlive(activity)) {
                DetailsActivity.app = app;
                showPackageIdDialog(app.getPackageName());
            } else {
                activity.finish();
            }
        }

        private DialogWrapperAbstract showPackageIdDialog(final String packageId) {
            return new DialogWrapper(activity)
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
