package com.github.yeriomin.yalpstore.view;

import android.content.Context;

import com.github.yeriomin.yalpstore.BlackWhiteListManager;
import com.github.yeriomin.yalpstore.R;

public class InstalledAppBadge extends AppBadge {

    @Override
    public void draw() {
        line2.clear();
        line3.clear();
        Context c = view.getContext();
        BlackWhiteListManager manager = new BlackWhiteListManager(c);
        if (manager.contains(app.getPackageName())) {
            line2.add(c.getString(manager.isBlack() ? R.string.list_app_blacklisted : R.string.list_app_whitelisted));
        }
        if (app.isSystem()) {
            line3.add(c.getString(R.string.list_app_system));
        }
        super.draw();
    }
}
