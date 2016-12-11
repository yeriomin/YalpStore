package com.github.yeriomin.yalpstore;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.github.yeriomin.yalpstore.model.App;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract public class AppListActivity extends ListActivity {

    public static final String PREFERENCE_EMAIL = "PREFERENCE_EMAIL";
    public static final String PREFERENCE_PASSWORD = "PREFERENCE_PASSWORD";
    public static final String PREFERENCE_AUTH_TOKEN = "PREFERENCE_AUTH_TOKEN";
    public static final String PREFERENCE_GSF_ID = "PREFERENCE_GSF_ID";

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
                intent.putExtra(DetailsActivity.INTENT_PACKAGE_NAME, (String) data.get(position).get(PACKAGE_NAME));
                startActivity(intent);
            }
        });

        loadApps();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                new AlertDialog.Builder(this)
                    .setMessage(R.string.dialog_message_logout)
                    .setTitle(R.string.dialog_title_logout)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            new PlayStoreApiWrapper(getApplicationContext()).logout();
                            dialogInterface.dismiss();
                            finish();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .show();
                break;
            case R.id.action_search:
                onSearchRequested();
                break;
            case R.id.action_updates:
                startActivity(new Intent(this, UpdatableAppsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
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

    protected List<App> getInstalledApps() {
        List<App> apps = new ArrayList<>();

        PackageManager pm = getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(PackageManager.GET_META_DATA);
        for (PackageInfo packageInfo : packages) {
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                // This is a system app - skipping
                continue;
            }
            App app = new App(packageInfo);
            app.setDisplayName(pm.getApplicationLabel(packageInfo.applicationInfo).toString());
            app.setIcon(pm.getApplicationIcon(packageInfo.applicationInfo));
            app.setInstalled(true);
            apps.add(app);
        }
        return apps;
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

    class ImageDownloadTask extends AsyncTask<String, Void, Void> {

        private ImageView view;
        private Context context;
        private Drawable drawable;

        public void setView(ImageView view) {
            this.view = view;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.context = this.view.getContext();
            this.view.setImageDrawable(this.context.getResources().getDrawable(android.R.drawable.sym_def_app_icon));
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            this.view.setImageDrawable(this.drawable);
        }

        @Override
        protected Void doInBackground(String[] params) {
            BitmapManager manager = new BitmapManager(this.context);
            this.drawable = new BitmapDrawable(manager.getBitmap(params[0]));
            return null;
        }

    }

}
