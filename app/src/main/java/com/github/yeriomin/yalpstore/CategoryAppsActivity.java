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
import android.util.Log;

import com.github.yeriomin.yalpstore.fragment.FilterMenu;
import com.github.yeriomin.yalpstore.task.playstore.CategoryAppsTask;
import com.github.yeriomin.yalpstore.task.playstore.EndlessScrollTask;

public class CategoryAppsActivity extends EndlessScrollActivity {

    static private final String INTENT_CATEGORY_ID = "INTENT_CATEGORY_ID";

    static public void start(Context context, String categoryId) {
        Intent intent = new Intent(context, CategoryAppsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(INTENT_CATEGORY_ID, categoryId);
        context.startActivity(intent);
    }

    private String categoryId;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        String newCategoryId = intent.getStringExtra(INTENT_CATEGORY_ID);
        if (null == newCategoryId) {
            Log.w(getClass().getSimpleName(), "No category id");
            return;
        }
        if (null == categoryId || !newCategoryId.equals(categoryId)) {
            categoryId = newCategoryId;
            setTitle(new CategoryManager(this).getCategoryName(categoryId));
            clearApps();
            loadApps();
        }
    }

    @Override
    protected EndlessScrollTask getTask() {
        CategoryAppsTask task = new CategoryAppsTask(iterator);
        task.setCategoryId(categoryId);
        task.setFilter(new FilterMenu(this).getFilterPreferences());
        return task;
    }
}
