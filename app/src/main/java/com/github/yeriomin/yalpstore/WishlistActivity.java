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

import android.content.Intent;
import android.os.Bundle;

import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.task.playstore.CloneableTask;
import com.github.yeriomin.yalpstore.task.playstore.WishlistUpdateTask;
import com.github.yeriomin.yalpstore.view.ListItem;
import com.github.yeriomin.yalpstore.view.SearchResultAppBadge;

import java.util.List;

public class WishlistActivity extends AppListActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
        loadApps();
    }

    @Override
    public void loadApps() {
        WishlistAppsTask task = new WishlistAppsTask(this);
        task.setProgressIndicator(findViewById(R.id.progress));
        task.execute();
    }

    @Override
    protected ListItem buildListItem(App app) {
        SearchResultAppBadge appBadge = new SearchResultAppBadge();
        appBadge.setApp(app);
        return appBadge;
    }

    private static class WishlistAppsTask extends WishlistUpdateTask {

        private WishlistActivity activity;

        public WishlistAppsTask(WishlistActivity activity) {
            this.activity = activity;
            setContext(activity);
        }

        @Override
        public CloneableTask clone() {
            WishlistAppsTask task = new WishlistAppsTask(activity);
            task.setErrorView(errorView);
            task.setProgressIndicator(progressIndicator);
            task.setContext(context);
            return task;
        }

        @Override
        protected void onPostExecute(List<String> result) {
            super.onPostExecute(result);
            activity.clearApps();
            activity.addApps(apps);
            if (apps.isEmpty()) {
                errorView.setText(R.string.list_empty_search);
            }
        }
    }
}
