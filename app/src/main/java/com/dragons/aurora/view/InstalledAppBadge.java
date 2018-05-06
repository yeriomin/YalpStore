package com.dragons.aurora.view;

import android.content.Context;
import android.widget.ImageView;

import com.dragons.aurora.BlackWhiteListManager;
import com.dragons.aurora.R;

public class InstalledAppBadge extends AppBadge {

    @Override
    public void draw() {
        line2.clear();
        line3.clear();
        Context c = view.getContext();
        BlackWhiteListManager manager = new BlackWhiteListManager(c);
        line2.add("v" + app.getVersionName() + "." + app.getVersionCode());
        if (app.isSystem())
            line3.add(c.getString(R.string.list_app_system));
        else
            line3.add(c.getString(R.string.list_app_user));
        if (manager.contains(app.getPackageName())) {
            line3.add(c.getString(manager.isBlack() ? R.string.list_app_blacklisted : R.string.list_app_whitelisted));
        }
        drawIcon((ImageView) view.findViewById(R.id.icon));
        super.draw();
    }
}
