package com.github.yeriomin.yalpstore;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
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

    abstract protected void loadApps();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.applist_activity_layout);

        setListAdapter(getSimpleListAdapter());
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra(DetailsActivity.INTENT_PACKAGE_NAME, (String) data.get(position).get(PACKAGE_NAME));
                startActivity(intent);
            }
        });
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
            public boolean setViewValue(final View view, Object drawableOrUrl, String textRepresentation) {
                if (view instanceof ImageView) {
                    if (drawableOrUrl instanceof String) {
                        ImageDownloadTask task = new ImageDownloadTask();
                        task.setView((ImageView) view);
                        task.execute((String) drawableOrUrl);
                    } else {
                        ((ImageView) view).setImageDrawable((Drawable) drawableOrUrl);
                    }
                    return true;
                }
                return false;
            }
        });
        return adapter;
    }

    protected ListAdapter mAdapter;
    protected ListView mList;

    private Handler mHandler = new Handler();
    private boolean mFinishedStart = false;

    private Runnable mRequestFocus = new Runnable() {
        public void run() {
            mList.focusableViewAvailable(mList);
        }
    };

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacks(mRequestFocus);
        super.onDestroy();
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        View emptyView = findViewById(android.R.id.empty);
        mList = (ListView) findViewById(android.R.id.list);
        if (mList == null) {
            throw new RuntimeException(
                "Your content must have a ListView whose id attribute is " +
                    "'android.R.id.list'");
        }
        if (emptyView != null) {
            mList.setEmptyView(emptyView);
        }
        if (mFinishedStart) {
            setListAdapter(mAdapter);
        }
        mHandler.post(mRequestFocus);
        mFinishedStart = true;
    }

    public void setListAdapter(ListAdapter adapter) {
        synchronized (this) {
            mAdapter = adapter;
            mList.setAdapter(adapter);
        }
    }

    public ListView getListView() {
        return mList;
    }

    public ListAdapter getListAdapter() {
        return mAdapter;
    }
}
