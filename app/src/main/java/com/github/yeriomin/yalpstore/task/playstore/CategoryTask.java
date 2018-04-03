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

package com.github.yeriomin.yalpstore.task.playstore;

import android.net.Uri;
import android.text.TextUtils;
import android.widget.ArrayAdapter;

import com.github.yeriomin.playstoreapi.BrowseLink;
import com.github.yeriomin.playstoreapi.BrowseResponse;
import com.github.yeriomin.playstoreapi.GooglePlayAPI;
import com.github.yeriomin.yalpstore.CategoryManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

abstract public class CategoryTask extends PlayStorePayloadTask<Void> {

    protected CategoryManager manager;

    abstract protected void fill();

    public void setManager(CategoryManager manager) {
        this.manager = manager;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        if (success()) {
            fill();
        }
    }

    @Override
    protected Void doInBackground(String... arguments) {
        if (manager.categoryListEmpty()) {
            super.doInBackground(arguments);
        }
        return null;
    }

    @Override
    protected Void getResult(GooglePlayAPI api, String... arguments) throws IOException {
        Map<String, String> topCategories = buildCategoryMap(api.categories());
        manager.save(CategoryManager.TOP, topCategories);
        for (String categoryId: topCategories.keySet()) {
            manager.save(categoryId, buildCategoryMap(api.categories(categoryId)));
        }
        return null;
    }

    protected ArrayAdapter getAdapter(Map<String, String> categories, int itemLayoutId) {
        return new ArrayAdapter<>(
            context,
            itemLayoutId,
            new ArrayList<>(categories.values())
        );
    }

    private Map<String, String> buildCategoryMap(BrowseResponse response) {
        Map<String, String> categories = new HashMap<>();
        for (BrowseLink category: response.getCategoryContainer().getCategoryList()) {
            String categoryId = Uri.parse(category.getDataUrl()).getQueryParameter("cat");
            if (TextUtils.isEmpty(categoryId)) {
                continue;
            }
            categories.put(categoryId, category.getName());
        }
        return categories;
    }
}
