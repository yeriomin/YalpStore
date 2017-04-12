package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.SimpleAdapter;

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
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        String newCategoryId = intent.getStringExtra(INTENT_CATEGORY_ID);
        if (null == newCategoryId) {
            Log.w(getClass().getName(), "No category id");
            return;
        }
        if (null == categoryId || !newCategoryId.equals(categoryId)) {
            this.data.clear();
            categoryId = newCategoryId;
            setTitle(new CategoryManager(this).getCategoryName(categoryId));
            loadApps();
            ((SimpleAdapter) getListView().getAdapter()).notifyDataSetChanged();
        }
    }

    @Override
    public void loadApps() {
        CategoryAppsTask task = new CategoryAppsTask() {

            @Override
            protected void onPostExecute(Throwable e) {
                super.onPostExecute(e);
                addApps(apps);
            }
        };
        prepareTask(task).execute(categoryId);
    }
}
