package com.dragons.aurora.task.playstore;

import com.dragons.aurora.AppListIterator;
import com.dragons.aurora.CategoryManager;
import com.dragons.aurora.activities.AuroraActivity;
import com.dragons.aurora.fragment.FilterMenu;
import com.dragons.aurora.model.App;
import com.dragons.aurora.model.Filter;

import java.util.ArrayList;
import java.util.List;

public class SearchTask extends CategoryAppsTask {

    @Override
    protected List<App> getNextBatch(AppListIterator iterator) {
        CategoryManager categoryManager = new CategoryManager(getContext());
        Filter filter = new FilterMenu((AuroraActivity) getContext()).getFilterPreferences();
        List<App> apps = new ArrayList<>();
        for (App app : iterator.next()) {
            if (categoryManager.fits(app.getCategoryId(), filter.getCategory())) {
                apps.add(app);
            }
        }
        return apps;
    }
}
