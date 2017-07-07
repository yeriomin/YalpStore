package com.github.yeriomin.yalpstore;

import com.github.yeriomin.playstoreapi.CategoryAppsIterator;
import com.github.yeriomin.playstoreapi.GooglePlayAPI;

import java.io.IOException;

class CategoryAppsTask extends EndlessScrollTask {

    private String categoryId;

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public CategoryAppsTask(AppListIterator iterator) {
        super(iterator);
    }

    @Override
    protected AppListIterator initIterator() throws IOException {
        return new AppListIterator(new CategoryAppsIterator(
            new PlayStoreApiAuthenticator(context).getApi(),
            categoryId,
            GooglePlayAPI.SUBCATEGORY.TOP_FREE
        ));
    }
}
