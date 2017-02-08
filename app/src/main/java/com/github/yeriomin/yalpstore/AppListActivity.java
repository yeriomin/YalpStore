package com.github.yeriomin.yalpstore;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.github.yeriomin.yalpstore.model.App;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract public class AppListActivity extends YalpStoreActivity {

    protected static final String LINE1 = "LINE1";
    protected static final String LINE2 = "LINE2";
    protected static final String ICON = "ICON";
    protected static final String PACKAGE_NAME = "PACKAGE_NAME";

    protected List<Map<String, Object>> data = new ArrayList<>();

    protected ListAdapter listAdapter;
    protected ListView listView;

    private boolean finishedStart = false;

    abstract protected void loadApps();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.applist_activity_layout);

        setListAdapter(getSimpleListAdapter());
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra(DetailsActivity.INTENT_PACKAGE_NAME, (String) data.get(position).get(PACKAGE_NAME));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        View emptyView = findViewById(android.R.id.empty);
        listView = (ListView) findViewById(android.R.id.list);
        if (emptyView != null) {
            listView.setEmptyView(emptyView);
        }
        if (finishedStart) {
            setListAdapter(listAdapter);
        }
        finishedStart = true;
    }

    protected Map<String, Object> formatApp(App app) {
        Map<String, Object> map = new HashMap<>();
        map.put(LINE1, app.getDisplayName());
        map.put(PACKAGE_NAME, app.getPackageName());
        return map;
    }

    protected void addApps(List<App> apps) {
        for (App app: apps) {
            data.add(this.formatApp(app));
        }
        ((SimpleAdapter) getListAdapter()).notifyDataSetChanged();
    }

    private SimpleAdapter getSimpleListAdapter() {

        String[] from = { LINE1, LINE2, ICON };
        int[] to = { R.id.text1, R.id.text2, R.id.icon  };

        SimpleAdapter adapter = new SimpleAdapter(
            this,
            data,
            R.layout.two_line_list_item_with_icon,
            from,
            to);

        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {

            @Override
            public boolean setViewValue(View view, Object drawableOrUrl, String textRepresentation) {
                if (!(view instanceof ImageView)) {
                    return false;
                }
                if (drawableOrUrl instanceof String) {
                    ImageDownloadTask task = new ImageDownloadTask();
                    task.setView((ImageView) view);
                    task.execute((String) drawableOrUrl);
                } else {
                    ((ImageView) view).setImageDrawable((Drawable) drawableOrUrl);
                }
                return true;
            }
        });
        return adapter;
    }

    public void setListAdapter(ListAdapter adapter) {
        synchronized (this) {
            listAdapter = adapter;
            listView.setAdapter(adapter);
        }
    }

    public ListView getListView() {
        return listView;
    }

    public ListAdapter getListAdapter() {
        return listAdapter;
    }
}
