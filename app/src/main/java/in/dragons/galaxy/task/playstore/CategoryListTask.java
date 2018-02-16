package in.dragons.galaxy.task.playstore;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Map;

import in.dragons.galaxy.CategoryAppsActivity;
import in.dragons.galaxy.CategoryListActivity;

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
        ListView list = (ListView) activity.findViewById(android.R.id.list);
        list.setAdapter(getAdapter(categories, android.R.layout.simple_list_item_1));
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CategoryAppsActivity.start(activity, new ArrayList<>(categories.keySet()).get(position));
            }
        });
    }
}
