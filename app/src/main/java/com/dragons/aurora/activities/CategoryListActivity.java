package com.dragons.aurora.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;

import com.percolate.caffeine.ViewUtils;

import java.util.Map;

import com.dragons.aurora.adapters.AllCategoriesAdapter;
import com.dragons.aurora.CategoryManager;
import com.dragons.aurora.R;
import com.dragons.aurora.adapters.TopCategoriesAdapter;
import com.dragons.aurora.task.playstore.CategoryListTask;
import com.dragons.aurora.task.playstore.CategoryTask;


public class CategoryListActivity extends AuroraActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = ViewUtils.findViewById(this, R.id.content_frame);
        getLayoutInflater().inflate(R.layout.app_category_inc, contentFrameLayout);
        setTitle(getString(R.string.action_categories));

        CategoryManager manager = new CategoryManager(this);
        getTask(manager).execute();
    }

    private CategoryTask getTask(CategoryManager manager) {
        CategoryListTask task = new CategoryListTask();
        task.setContext(this);
        task.setManager(manager);
        task.setErrorView(ViewUtils.findViewById(this, R.id.empty));
        task.setProgressIndicator(findViewById(R.id.progress));
        return task;
    }

    public void setupTopCategories() {
        RecyclerView recyclerView = ViewUtils.findViewById(this, R.id.top_cat_view);
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
        ViewUtils.findViewById(this, R.id.cat_container).setVisibility(View.VISIBLE);
    }

    public void setupAllCategories(Map<String, String> categories) {
        RecyclerView recyclerView = ViewUtils.findViewById(this, R.id.all_cat_view);
        RecyclerView.LayoutManager rlm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(rlm);
        RecyclerView.Adapter rva = new AllCategoriesAdapter(this, categories);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {
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
    }
}
