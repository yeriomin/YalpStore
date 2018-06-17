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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.github.yeriomin.yalpstore.task.EventsTask;

public class EventsActivity extends ListActivity {

    static private final String INTENT_PACKAGE_NAME = "INTENT_PACKAGE_NAME";

    static public Intent getDetailsIntent(Context context, String packageName) {
        Intent intent = new Intent(context, EventsActivity.class);
        if (!TextUtils.isEmpty(packageName)) {
            intent.putExtra(INTENT_PACKAGE_NAME, packageName);
        }
        if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity_layout);
        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        EventsTask task = new EventsTask();
        task.setContext(this);
        task.setProgressIndicator(findViewById(R.id.progress));
        if (!TextUtils.isEmpty(intent.getStringExtra(INTENT_PACKAGE_NAME))) {
            task.setPackageName(intent.getStringExtra(INTENT_PACKAGE_NAME));
        }
        task.executeOnExecutorIfPossible();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.event_list_item;
    }
}
