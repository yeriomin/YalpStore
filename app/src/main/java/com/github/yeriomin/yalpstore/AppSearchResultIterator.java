package com.github.yeriomin.yalpstore;

import com.github.yeriomin.playstoreapi.DocV2;
import com.github.yeriomin.playstoreapi.SearchIterator;
import com.github.yeriomin.playstoreapi.SearchResponse;
import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.model.AppBuilder;

import java.util.ArrayList;
import java.util.List;

class AppSearchResultIterator extends AppListIterator {

    public AppSearchResultIterator(SearchIterator iterator) {
        super(iterator);
    }

    public String getQuery() {
        return ((SearchIterator) iterator).getQuery();
    }

    @Override
    public List<App> next() {
        List<App> apps = new ArrayList<>();
        SearchResponse response = ((SearchIterator) iterator).next();
        for (DocV2 details : response.getDocList().get(0).getChildList()) {
            addApp(apps, AppBuilder.build(details));
        }
        return apps;
    }
}
