package com.github.yeriomin.yalpstore;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.model.App;

import java.util.HashMap;
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
        String updated = app.getUpdated();
        if (null != updated && !updated.isEmpty()) {
            map.put(LINE2, getString(R.string.list_line_2_updatable, updated));
        }
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
                    data.add(getHeader(R.string.list_has_update));
                    addApps(this.updatableApps);
                    data.add(getHeader(R.string.list_no_update));
                    addApps(this.otherInstalledApps);
                    toggleUpdateAll(this.updatableApps.size() > 0);
                    new CategoryManager(UpdatableAppsActivity.this).downloadCategoryNames();
                }
            }
        };
        task.setErrorView((TextView) getListView().getEmptyView());
        task.setContext(this);
        task.setProgressIndicator(findViewById(R.id.progress));
        return task;
    }

    private Map<String, Object> getHeader(int headerTextResId) {
        Map<String, Object> map = new HashMap<>();
        map.put(LINE1, getString(headerTextResId));
        return map;
    }

    private void toggleUpdateAll(boolean enable) {
        Button button = (Button) findViewById(R.id.update_all);
        button.setVisibility((enable && backgroundUpdatesEnabled()) ? View.VISIBLE : View.GONE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UpdateChecker().onReceive(v.getContext(), getIntent());
            }
        });
    }

    private boolean backgroundUpdatesEnabled() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getBoolean(PreferenceActivity.PREFERENCE_BACKGROUND_UPDATE_INSTALL, false);
    }
}

