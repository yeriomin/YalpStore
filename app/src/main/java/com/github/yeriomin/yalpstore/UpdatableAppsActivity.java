package com.github.yeriomin.yalpstore;

import android.os.Bundle;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.model.App;

import java.util.Map;

public class UpdatableAppsActivity extends AppListActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(getString(R.string.activity_title_updates));
        ((TextView) getListView().getEmptyView()).setText(getString(R.string.list_empty_updates));
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.data.clear();
        loadApps();
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
                }
            }
        };
        task.setErrorView((TextView) getListView().getEmptyView());
        task.setContext(this);
        task.prepareDialog(
            getString(R.string.dialog_message_loading_app_list_update),
            getString(R.string.dialog_title_loading_app_list_update)
        );
        return task;
    }
}

