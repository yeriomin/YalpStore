package com.dragons.aurora.task.playstore;

import android.text.TextUtils;

import com.dragons.aurora.CategoryManager;
import com.dragons.aurora.playstoreapiv2.DocV2;
import com.dragons.aurora.playstoreapiv2.GooglePlayAPI;
import com.dragons.aurora.playstoreapiv2.ListResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CategoryListTaskHelper extends ExceptionTaskHelper {


    protected boolean getResult(GooglePlayAPI api, CategoryManager manager) throws IOException {
        Map<String, String> topCategories = buildCategoryMap(api.categoriesList());
        manager.save(CategoryManager.TOP, topCategories);
        for (String categoryId : topCategories.keySet()) {
            manager.save(categoryId, buildCategoryMap(api.categoriesList(categoryId)));
        }
        return true;
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