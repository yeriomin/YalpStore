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

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.github.yeriomin.yalpstore.fragment.FilterMenu;
import com.github.yeriomin.yalpstore.task.playstore.ClusterTask;

public class ClusterActivity extends EndlessScrollActivity {

    static private final String INTENT_URL = "INTENT_URL";
    static private final String INTENT_TITLE = "INTENT_TITLE";

    private String clusterUrl;

    static public void start(Context context, String url, String title) {
        Intent intent = new Intent(context, ClusterActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(ClusterActivity.INTENT_URL, url);
        intent.putExtra(ClusterActivity.INTENT_TITLE, title);
        context.startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (TextUtils.isEmpty(intent.getStringExtra(ClusterActivity.INTENT_URL))
            || TextUtils.isEmpty(intent.getStringExtra(ClusterActivity.INTENT_TITLE))
        ) {
            Log.w(getClass().getSimpleName(), "No cluster url or title provided in the intent");
            finish();
            return;
        }

        setTitle(intent.getStringExtra(ClusterActivity.INTENT_TITLE));
        clusterUrl = intent.getStringExtra(ClusterActivity.INTENT_URL);
        clearApps();
        loadApps();
    }

    @Override
    protected ClusterTask getTask() {
        ClusterTask task = new ClusterTask(iterator);
        task.setFilter(new FilterMenu(this).getFilterPreferences());
        task.setClusterUrl(clusterUrl);
        return task;
    }
}
