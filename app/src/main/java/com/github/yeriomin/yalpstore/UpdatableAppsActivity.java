package com.github.yeriomin.yalpstore;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.model.App;

import java.util.Map;

public class UpdatableAppsActivity extends AppListActivity {

    static private boolean needsUpdate;

    static public void setNeedsUpdate(boolean needsUpdate) {
        UpdatableAppsActivity.needsUpdate = needsUpdate;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setNeedsUpdate(true);
        setTitle(getString(R.string.activity_title_updates));
        ((TextView) getListView().getEmptyView()).setText(getString(R.string.list_empty_updates));
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (UpdatableAppsActivity.needsUpdate) {
            this.data.clear();
            loadApps();
            setNeedsUpdate(false);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
        setNeedsUpdate(true);
    }

    @Override
    protected Map<String, Object> formatApp(App app) {
        Map<String, Object> map = super.formatApp(app);
        map.put(LINE2, getString(R.string.list_line_2_updatable, app.getUpdated()));
        map.put(ICON, app.getIcon());
        return map;
    }

    protected void loadApps() {
        UpdatableAppsTask taskClone = getTask();
        UpdatableAppsTask task = getTask();
        task.setTaskClone(taskClone);
        task.execute();
    }

    private UpdatableAppsTask getTask() {
        UpdatableAppsTask task = new UpdatableAppsTask() {
            @Override
            protected void onPostExecute(Throwable e) {
                super.onPostExecute(e);
                if (null == e) {
                    addApps(this.apps);
                    new CategoryManager(UpdatableAppsActivity.this).downloadCategoryNames();
                }
            }
        };
        task.setErrorView((TextView) getListView().getEmptyView());
        task.setContext(this);
        task.setProgressIndicator(findViewById(R.id.progress));
        return task;
    }
}

