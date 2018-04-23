package com.dragons.aurora.task.playstore;

import com.dragons.aurora.AppListIterator;
import com.dragons.aurora.PlayStoreApiAuthenticator;
import com.dragons.aurora.playstoreapiv2.CategoryAppsIterator;
import com.dragons.aurora.playstoreapiv2.GooglePlayAPI;

import java.io.IOException;

public class CategoryAppsTask extends EndlessScrollTask implements CloneableTask {

    private String categoryId;
    private GooglePlayAPI.SUBCATEGORY subCategory;

    public CategoryAppsTask(AppListIterator iterator) {
        super(iterator);
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public void setSubCategory(GooglePlayAPI.SUBCATEGORY subCategory) {
        this.subCategory = subCategory;
    }

    @Override
    public CloneableTask clone() {
        CategoryAppsTask task = new CategoryAppsTask(iterator);
        task.setFilter(filter);
        task.setCategoryId(categoryId);
        task.setErrorView(errorView);
        task.setContext(context);
        task.setProgressIndicator(progressIndicator);
        return task;
    }

    @Override
    protected AppListIterator initIterator() throws IOException {
        return new AppListIterator(new CategoryAppsIterator(
                new PlayStoreApiAuthenticator(context).getApi(),
                categoryId,
                subCategory));
    }
}
