package in.dragons.galaxy;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import in.dragons.galaxy.task.playstore.CategoryListTask;
import in.dragons.galaxy.task.playstore.CategoryTask;


public class CategoryListActivity extends GalaxyActivity {
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        setTheme(sharedPreferences.getBoolean("THEME", true) ? R.style.AppTheme : R.style.AppTheme_Dark);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categories_activity_layout);
        super.onCreateDrawer(savedInstanceState);
        setTitle(getString(R.string.action_categories));

        CategoryManager manager = new CategoryManager(this);
        getTask(manager).execute();
    }

    public void setupTopCategories() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.top_cat_view);
        RecyclerView.LayoutManager rlm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(rlm);
        RecyclerView.Adapter rva = new TopCategoriesAdapter(this, getResources().getStringArray(R.array.topCategories));
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false) {
            @Override
            public boolean canScrollHorizontally() {
                return true;
            }

            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        recyclerView.setAdapter(rva);
        com.percolate.caffeine.ViewUtils.findViewById(this, R.id.cat_container).setVisibility(View.VISIBLE);
    }

    private CategoryTask getTask(CategoryManager manager) {
        CategoryListTask task = new CategoryListTask();
        task.setContext(this);
        task.setManager(manager);
        task.setErrorView((TextView) findViewById(R.id.empty));
        task.setProgressIndicator(findViewById(R.id.progress));
        return task;
    }
}
