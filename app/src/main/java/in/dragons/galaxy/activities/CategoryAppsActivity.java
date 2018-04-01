package in.dragons.galaxy.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.percolate.caffeine.ViewUtils;

import in.dragons.galaxy.CategoryManager;
import in.dragons.galaxy.R;
import in.dragons.galaxy.fragment.FilterMenu;
import in.dragons.galaxy.task.playstore.CategoryAppsTask;
import in.dragons.galaxy.task.playstore.EndlessScrollTask;

public class CategoryAppsActivity extends EndlessScrollActivity {

    static private final String INTENT_CATEGORY_ID = "INTENT_CATEGORY_ID";

    static public void start(Context context, String categoryId) {
        Intent intent = new Intent(context, CategoryAppsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(INTENT_CATEGORY_ID, categoryId);
        context.startActivity(intent);
    }

    private String categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = ViewUtils.findViewById(this, R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        String newCategoryId = intent.getStringExtra(INTENT_CATEGORY_ID);
        if (null == newCategoryId) {
            Log.w(getClass().getSimpleName(), "No category id");
            return;
        }
        if (null == categoryId || !newCategoryId.equals(categoryId)) {
            categoryId = newCategoryId;
            setTitle(new CategoryManager(this).getCategoryName(categoryId));
            clearApps();
            loadApps(subCategory);
        }
    }

    @Override
    protected EndlessScrollTask getTask() {
        CategoryAppsTask task = new CategoryAppsTask(iterator);
        task.setCategoryId(categoryId);
        task.setFilter(new FilterMenu(this).getFilterPreferences());
        return task;
    }
}
