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
import android.preference.PreferenceManager;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class CategoryManager {

    public static final String TOP = "0_CATEGORY_TOP";

    private Context context;
    private SharedPreferencesTranslator translator;

    public CategoryManager(Context context) {
        this.context = context;
        translator = new SharedPreferencesTranslator(PreferenceManager.getDefaultSharedPreferences(context));
    }

    public String getCategoryName(String categoryId) {
        if (null == categoryId) {
            return "";
        }
        if (categoryId.equals(TOP)) {
            return context.getString(R.string.search_filter);
        }
        return translator.getString(categoryId);
    }

    public void save(String parent, Map<String, String> categories) {
        PreferenceUtil.putStringSet(context, parent, categories.keySet());
        for (String categoryId: categories.keySet()) {
            translator.putString(categoryId, categories.get(categoryId));
        }
    }

    public boolean fits(String appCategoryId, String chosenCategoryId) {
        return null == chosenCategoryId
            || chosenCategoryId.equals(TOP)
            || appCategoryId.equals(chosenCategoryId)
            || PreferenceUtil.getStringSet(context, chosenCategoryId).contains(appCategoryId)
        ;
    }

    public boolean categoryListEmpty() {
        Set<String> topSet = PreferenceUtil.getStringSet(context, TOP);
        if (topSet.isEmpty()) {
            return true;
        }
        int size = topSet.size();
        String categoryId = topSet.toArray(new String[size])[size - 1];
        return translator.getString(categoryId).equals(categoryId);
    }

    public Map<String, String> getCategoriesFromSharedPreferences() {
        Map<String, String> categories = new TreeMap<>();
        Set<String> topSet = PreferenceUtil.getStringSet(context, TOP);
        for (String topCategoryId: topSet) {
            categories.put(topCategoryId, translator.getString(topCategoryId));
            Set<String> subSet = PreferenceUtil.getStringSet(context, topCategoryId);
            for (String subCategoryId: subSet) {
                categories.put(subCategoryId, categories.get(topCategoryId) + " - " + translator.getString(subCategoryId));
            }
        }
        return Util.sort(categories);
    }
}
