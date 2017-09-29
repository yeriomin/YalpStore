package com.github.yeriomin.playstoreapi;

import java.util.HashMap;
import java.util.Map;

public class CategoryAppsIterator extends AppListIterator {

    private String categoryId;

    public CategoryAppsIterator(GooglePlayAPI googlePlayApi, String categoryId, GooglePlayAPI.SUBCATEGORY subcategory) {
        super(googlePlayApi);
        this.categoryId = categoryId;
        String url = GooglePlayAPI.LIST_URL;
        Map<String, String> params = new HashMap<String, String>();
        params.put("cat", categoryId);
        params.put("ctr", subcategory.value);
        firstPageUrl = googlePlayApi.getClient().buildUrl(url, params);
    }

    public String getCategoryId() {
        return categoryId;
    }
}
