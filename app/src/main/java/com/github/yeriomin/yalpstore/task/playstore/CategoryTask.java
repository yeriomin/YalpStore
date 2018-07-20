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

import android.text.TextUtils;

import com.github.yeriomin.playstoreapi.DocV2;
import com.github.yeriomin.playstoreapi.GooglePlayAPI;
import com.github.yeriomin.playstoreapi.ListResponse;
import com.github.yeriomin.yalpstore.CategoryManager;

import java.io.IOException;
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
        Map<String, String> topCategories = buildCategoryMap(api.categoriesList());
        manager.save(CategoryManager.TOP, topCategories);
        for (String categoryId: topCategories.keySet()) {
            manager.save(categoryId, buildCategoryMap(api.categoriesList(categoryId)));
        }
        return null;
    }

    private Map<String, String> buildCategoryMap(ListResponse response) {
        Map<String, String> categories = new HashMap<>();
        for (DocV2 categoryCluster: response.getDoc(0).getChildList()) {
            if (!categoryCluster.getBackendDocid().equals("category_list_cluster")) {
                continue;
            }
            for (DocV2 category: categoryCluster.getChildList()) {
                if (!category.hasUnknownCategoryContainer()
                    || !category.getUnknownCategoryContainer().hasCategoryIdContainer()
                    || !category.getUnknownCategoryContainer().getCategoryIdContainer().hasCategoryId()
                ) {
                    continue;
                }
                String categoryId = category.getUnknownCategoryContainer().getCategoryIdContainer().getCategoryId();
                if (TextUtils.isEmpty(categoryId)) {
                    continue;
                }
                categories.put(categoryId, category.getTitle());
            }
        }
        return categories;
    }
}
