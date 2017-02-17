package com.github.yeriomin.yalpstore;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

import com.github.yeriomin.yalpstore.model.App;

public class IgnoreOptionManager extends DetailsManager {

    private Menu menu;

    @Override
    public void draw() {
        MenuItem item = getIgnoreMenuItem();
        if (null != item && app.isInstalled()) {
            item.setVisible(true);
            updateBlackWhiteListItemTitle(item);
        }
    }

    public void setApp(App app) {
        this.app = app;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public IgnoreOptionManager(DetailsActivity activity, App app) {
        super(activity, app);
    }

    public void toggleBlackWhiteList() {
        MenuItem item = getIgnoreMenuItem();
        if (null == item || !app.isInstalled()) {
            return;
        }
        BlackWhiteListManager manager = new BlackWhiteListManager(activity);
        if (manager.contains(app.getPackageName())) {
            manager.remove(app.getPackageName());
        } else {
            manager.add(app.getPackageName());
        }
        updateBlackWhiteListItemTitle(item);
    }

    private MenuItem getIgnoreMenuItem() {
        if (null == menu) {
            return null;
        }
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item.getItemId() == R.id.action_ignore) {
                return item;
            }
        }
        return null;
    }

    private void updateBlackWhiteListItemTitle(MenuItem item) {
        BlackWhiteListManager manager = new BlackWhiteListManager(activity);
        boolean inList = manager.contains(app.getPackageName());
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        boolean isBlacklist = prefs.getString(
            PreferenceActivity.PREFERENCE_UPDATE_LIST_WHITE_OR_BLACK,
            PreferenceActivity.LIST_BLACK
        ).equals(PreferenceActivity.LIST_BLACK);
        item.setTitle(isBlacklist
            ? (inList ? R.string.action_unignore : R.string.action_ignore)
            : (inList ? R.string.action_unwhitelist : R.string.action_whitelist)
        );
    }
}
