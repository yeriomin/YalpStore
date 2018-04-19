package com.dragons.aurora.task.playstore;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.dragons.aurora.playstoreapiv2.BrowseLink;
import com.dragons.aurora.playstoreapiv2.BrowseResponse;
import com.dragons.aurora.playstoreapiv2.GooglePlayAPI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.dragons.aurora.CategoryManager;

abstract public class CategoryTask extends PlayStorePayloadTask<Void> {

    protected CategoryManager manager;

    public void setManager(CategoryManager manager) {
        this.manager = manager;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        if (success()) {
            Log.i(getClass().getSimpleName(), "Categories List Fetched");
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
        for (String categoryId : topCategories.keySet()) {
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
        for (BrowseLink category : response.getCategoryContainer().getCategoryList()) {
            String categoryId = Uri.parse(category.getDataUrl()).getQueryParameter("cat");
            if (TextUtils.isEmpty(categoryId)) {
                continue;
            }
            categories.put(categoryId, category.getName());
        }
        return categories;
    }
}
