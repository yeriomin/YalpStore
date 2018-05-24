package com.dragons.aurora.task.playstore;

import android.content.Context;
import android.text.TextUtils;

import com.dragons.aurora.CategoryManager;
import com.dragons.aurora.LocaleManager;
import com.dragons.aurora.PlayStoreApiAuthenticator;
import com.dragons.aurora.playstoreapiv2.DocV2;
import com.dragons.aurora.playstoreapiv2.GooglePlayAPI;
import com.dragons.aurora.playstoreapiv2.ListResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CategoryListTask extends ExceptionTask {

    protected boolean getResult(Context context) throws IOException {
        CategoryManager categoryManager = new CategoryManager(context);

        GooglePlayAPI api = new PlayStoreApiAuthenticator(context).getApi();
        api.setLocale(new Locale(LocaleManager.getLanguage(context)));

        Map<String, String> topCategories = buildCategoryMap(api.categoriesList());
        categoryManager.save(CategoryManager.TOP, topCategories);
        for (String categoryId : topCategories.keySet()) {
            categoryManager.save(categoryId, buildCategoryMap(api.categoriesList(categoryId)));
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