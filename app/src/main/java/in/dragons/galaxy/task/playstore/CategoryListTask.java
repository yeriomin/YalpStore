package in.dragons.galaxy.task.playstore;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.Map;

import in.dragons.galaxy.AllCategoriesAdapter;
import in.dragons.galaxy.CategoryListActivity;
import in.dragons.galaxy.R;

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

        RecyclerView recyclerView = (RecyclerView) activity.findViewById(R.id.all_cat_view);
        RecyclerView.LayoutManager rlm = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(rlm);
        RecyclerView.Adapter rva = new AllCategoriesAdapter(activity, manager.getCategoriesFromSharedPreferences());
        recyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }

            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        recyclerView.setAdapter(rva);
        activity.setupTopCategories();
    }
}
