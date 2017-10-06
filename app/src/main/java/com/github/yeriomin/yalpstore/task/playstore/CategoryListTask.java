package com.github.yeriomin.yalpstore.task.playstore;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.github.yeriomin.yalpstore.CategoryAppsActivity;
import com.github.yeriomin.yalpstore.CategoryListActivity;

import java.util.ArrayList;
import java.util.Map;

public class CategoryListTask extends CategoryTask implements CloneableTask {

    @Override
    public CloneableTask clone() {
        CategoryListTask task = new CategoryListTask();
        task.setManager(manager);
        task.setErrorView(errorView);
        task.setContext(context);
        task.setProgressIndicator(progressIndicator);
        return task;
    }

    @Override
    protected void fill() {
        final CategoryListActivity activity = (CategoryListActivity) context;
        final Map<String, String> categories = manager.getCategoriesFromSharedPreferences();
        ListView list = activity.findViewById(android.R.id.list);
        list.setAdapter(getAdapter(categories, android.R.layout.simple_list_item_1));
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CategoryAppsActivity.start(activity, new ArrayList<>(categories.keySet()).get(position));
            }
        });
    }
}
