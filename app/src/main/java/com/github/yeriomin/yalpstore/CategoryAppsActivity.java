package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.model.App;

import java.util.Map;

public class CategoryAppsActivity extends AppListActivity {

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

        onNewIntent(getIntent());
        getListView().setOnScrollListener(new ScrollEdgeListener() {
            protected void loadMore() {
                loadApps();
            }
        });
    }

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
            ((SimpleAdapter) getListAdapter()).notifyDataSetChanged();
        }
    }

    @Override
    protected Map<String, Object> formatApp(App app) {
        Map<String, Object> map = super.formatApp(app);
        String updated = app.getUpdated().isEmpty() ? getString(R.string.list_incompatible) : app.getUpdated();
        map.put(LINE2, getString(R.string.list_line_2_search, app.getInstalls(), app.getRating().getAverage(), updated));
        map.put(ICON, app.getIconUrl());
        return map;
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
        task.setContext(this);
        task.setErrorView((TextView) getListView().getEmptyView());
        if (data.isEmpty()) {
            task.prepareDialog(R.string.dialog_message_loading_app_list_search, R.string.dialog_title_loading_app_list_search);
        } else {
            task.setProgressIndicator(findViewById(R.id.progress));
        }
        task.execute(categoryId);
    }
}
