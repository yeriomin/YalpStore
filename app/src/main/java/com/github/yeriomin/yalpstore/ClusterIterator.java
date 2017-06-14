package com.github.yeriomin.yalpstore;

import com.github.yeriomin.playstoreapi.DocV2;
import com.github.yeriomin.playstoreapi.ListResponse;
import com.github.yeriomin.playstoreapi.UrlIterator;
import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.model.AppBuilder;

import java.util.ArrayList;
import java.util.List;

public class ClusterIterator extends AppListIterator {

    public ClusterIterator(UrlIterator iterator) {
        super(iterator);
    }

    @Override
    public List<App> next() {
        List<App> apps = new ArrayList<>();
        ListResponse response = ((UrlIterator) iterator).next();
        for (DocV2 details : response.getDocList().get(0).getChildList()) {
            addApp(apps, AppBuilder.build(details));
        }
        return apps;
    }
}
