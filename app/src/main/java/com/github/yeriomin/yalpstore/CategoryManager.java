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
import android.content.SharedPreferences;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class CategoryManager {

    public static final String TOP = "0_CATEGORY_TOP";

    private String allAppsLabel;
    private SharedPreferencesTranslator translator;
    private SharedPreferences prefs;

    public CategoryManager(Context context) {
        allAppsLabel = context.getString(R.string.search_filter);
        translator = new SharedPreferencesTranslator(context);
        this.prefs = context.getSharedPreferences(getClass().getName(), Context.MODE_PRIVATE);
    }

    public String getCategoryName(String categoryId) {
        if (null == categoryId) {
            return "";
        }
        if (categoryId.equals(TOP)) {
            return allAppsLabel;
        }
        return translator.getString(categoryId);
    }

    public void save(String parent, Map<String, String> categories) {
        PreferenceUtil.putStringSet(prefs, parent, categories.keySet());
        for (String categoryId: categories.keySet()) {
            translator.putString(categoryId, categories.get(categoryId));
        }
    }

    public boolean fits(String appCategoryId, String chosenCategoryId) {
        return null == chosenCategoryId
            || chosenCategoryId.equals(TOP)
            || appCategoryId.equals(chosenCategoryId)
            || PreferenceUtil.getStringSet(prefs, chosenCategoryId).contains(appCategoryId)
        ;
    }

    public boolean categoryListEmpty() {
        Set<String> topSet = PreferenceUtil.getStringSet(prefs, TOP);
        if (topSet.isEmpty()) {
            return true;
        }
        int size = topSet.size();
        String categoryId = topSet.toArray(new String[size])[size - 1];
        return translator.getString(categoryId).equals(categoryId);
    }

    public Map<String, String> getCategoriesFromSharedPreferences() {
        Map<String, String> categories = new TreeMap<>();
        Set<String> topSet = PreferenceUtil.getStringSet(prefs, TOP);
        for (String topCategoryId: topSet) {
            categories.put(topCategoryId, translator.getString(topCategoryId));
            Set<String> subSet = PreferenceUtil.getStringSet(prefs, topCategoryId);
            for (String subCategoryId: subSet) {
                categories.put(subCategoryId, categories.get(topCategoryId) + " - " + translator.getString(subCategoryId));
            }
        }
        return Util.sort(categories);
    }
}
