package com.dragons.aurora.task.playstore;

import android.text.TextUtils;
import android.widget.ArrayAdapter;

import com.dragons.aurora.CategoryManager;
import com.dragons.aurora.playstoreapiv2.DocV2;
import com.dragons.aurora.playstoreapiv2.GooglePlayAPI;
import com.dragons.aurora.playstoreapiv2.ListResponse;

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
        Map<String, String> topCategories = buildCategoryMap(api.categoriesList());
        manager.save(CategoryManager.TOP, topCategories);
        for (String categoryId : topCategories.keySet()) {
            manager.save(categoryId, buildCategoryMap(api.categoriesList(categoryId)));
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

    private Map<String, String> buildCategoryMap(ListResponse response) {
        Map<String, String> categories = new HashMap<>();
        for (DocV2 categoryCluster : response.getDoc(0).getChildList()) {
            if (!categoryCluster.getBackendDocid().equals("category_list_cluster")) {
                continue;
            }
            for (DocV2 category : categoryCluster.getChildList()) {
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
