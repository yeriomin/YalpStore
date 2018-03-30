package in.dragons.galaxy.task.playstore;

import java.util.Map;

import in.dragons.galaxy.activities.CategoryListActivity;

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

    protected void fill() {
        final CategoryListActivity activity = (CategoryListActivity) context;
        final Map<String, String> categories = manager.getCategoriesFromSharedPreferences();
        activity.setupTopCategories();
        activity.setupAllCategories(categories);
    }
}