package com.github.yeriomin.yalpstore.view;

import android.content.Context;
import android.text.TextUtils;

import com.github.yeriomin.yalpstore.PreferenceActivity;
import com.github.yeriomin.yalpstore.R;

public class UpdatableAppBadge extends AppBadge {

    @Override
    public void draw() {
        line2.clear();
        line3.clear();
        Context c = view.getContext();
        String updated = app.getUpdated();
        if (!TextUtils.isEmpty(updated)) {
            line2.add(c.getString(R.string.list_line_2_updatable, updated));
        }
        if (app.isSystem()) {
            line3.add(c.getString(R.string.list_app_system));
        }
        if (app.isInPlayStore() && !showUpdatesOnly()) {
            line3.add(c.getString(R.string.list_app_exists_in_play_store));
        }
        super.draw();
    }

    private boolean showUpdatesOnly() {
        return PreferenceActivity.getBoolean(view.getContext(), PreferenceActivity.PREFERENCE_UPDATES_ONLY);
    }
}
