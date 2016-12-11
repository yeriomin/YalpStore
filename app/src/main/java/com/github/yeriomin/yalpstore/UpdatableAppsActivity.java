package com.github.yeriomin.yalpstore;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.model.App;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdatableAppsActivity extends AppListActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((TextView) getListView().getEmptyView()).setText(getString(R.string.list_empty_updates));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (this.data.isEmpty()) {
            loadApps();
        }
    }

    @Override
    protected Map<String, Object> formatApp(App app) {
        Map<String, Object> map = super.formatApp(app);
        map.put(LINE2, getString(R.string.list_line_2_updatable, app.getUpdated()));
        map.put(ICON, app.getIcon());
        return map;
    }

    protected void loadApps() {
        GoogleApiAsyncTask task = new GoogleApiAsyncTask() {

            private List<App> apps = new ArrayList<>();

            @Override
            protected Throwable doInBackground(Void... params) {
                // Building local apps list
                List<String> installedAppIds = new ArrayList<>();
                List<App> installedApps = getInstalledApps();
                Map<String, App> appMap = new HashMap<>();
                for (App installedApp: installedApps) {
                    String packageName = installedApp.getPackageInfo().packageName;
                    installedAppIds.add(packageName);
                    appMap.put(packageName, installedApp);
                }
                // Requesting info from Google Play Market for installed apps
                PlayStoreApiWrapper wrapper = new PlayStoreApiWrapper(this.context);
                List<App> appsFromPlayMarket = new ArrayList<>();
                try {
                    appsFromPlayMarket.addAll(wrapper.getDetails(installedAppIds));
                } catch (Throwable e) {
                    return e;
                }
                // Comparing versions and building updatable apps list
                for (App appFromMarket: appsFromPlayMarket) {
                    String packageName = appFromMarket.getPackageName();
                    if (null == packageName || packageName.isEmpty()) {
                        continue;
                    }
                    App installedApp = appMap.get(packageName);
                    if (installedApp.getVersionCode() < appFromMarket.getVersionCode()) {
                        installedApp.setUpdated(appFromMarket.getUpdated());
                        installedApp.setVersionCode(appFromMarket.getVersionCode());
                        installedApp.setOfferType(appFromMarket.getOfferType());
                        apps.add(installedApp);
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Throwable e) {
                super.onPostExecute(e);
                addApps(apps);
            }
        };
        task.setErrorView((TextView) getListView().getEmptyView());
        task.setContext(this);
        task.prepareDialog(
            getString(R.string.dialog_message_loading_app_list_update),
            getString(R.string.dialog_title_loading_app_list_update)
        );
        task.execute();
    }

}

