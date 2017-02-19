package com.github.yeriomin.yalpstore;

import java.util.Map;

public class CategoryTask extends GoogleApiAsyncTask {

    private CategoryManager manager;

    public void setManager(CategoryManager manager) {
        this.manager = manager;
    }

    @Override
    protected Throwable doInBackground(String... params) {
        PlayStoreApiWrapper wrapper = new PlayStoreApiWrapper(this.context);
        try {
            Map<String, String> topCategories = wrapper.getCategories();
            manager.save(CategoryManager.TOP, topCategories);
            for (String categoryId: topCategories.keySet()) {
                manager.save(categoryId, wrapper.getCategories(categoryId));
            }
        } catch (Throwable e) {
            return e;
        }
        return null;
    }
}
