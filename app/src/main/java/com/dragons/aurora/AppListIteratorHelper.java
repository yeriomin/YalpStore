package com.dragons.aurora;

import com.dragons.aurora.model.App;
import com.dragons.aurora.model.AppBuilder;
import com.dragons.aurora.playstoreapiv2.DocV2;
import com.dragons.aurora.playstoreapiv2.GooglePlayAPI;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AppListIteratorHelper implements Iterator {

    protected com.dragons.aurora.playstoreapiv2.AppListIterator iterator;

    public AppListIteratorHelper(com.dragons.aurora.playstoreapiv2.AppListIterator iterator) {
        this.iterator = iterator;
    }

    public void setGooglePlayApi(GooglePlayAPI googlePlayApi) {
        iterator.setGooglePlayApi(googlePlayApi);
    }

    @Override
    public List<App> next() {
        List<App> apps = new ArrayList<>();
        for (DocV2 details : iterator.next()) {
            addApp(apps, AppBuilder.build(details));
        }
        return apps;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    private void addApp(List<App> apps, App app) {
        apps.add(app);
    }
}
