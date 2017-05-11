package com.github.yeriomin.yalpstore.view;

import android.content.Context;
import android.text.TextUtils;

import com.github.yeriomin.yalpstore.PreferenceActivity;
import com.github.yeriomin.yalpstore.R;

public class UpdatableAppBadge extends AppBadge {

    @Override
    public void draw() {
        super.draw();
        Context c = view.getContext();
        String updated = app.getUpdated();
        if (!TextUtils.isEmpty(updated)) {
            setText(R.id.text2, c.getString(R.string.list_line_2_updatable, updated));
        }
        String line3 = "";
        if (app.isSystem()) {
            line3 += c.getString(R.string.list_app_system) + " ";
        }
        if (app.isInPlayStore() && !showUpdatesOnly()) {
            line3 += c.getString(R.string.list_app_exists_in_play_store);
        }
        setText(R.id.text3, line3);
    }

    private boolean showUpdatesOnly() {
        return PreferenceActivity.getBoolean(view.getContext(), PreferenceActivity.PREFERENCE_UPDATES_ONLY);
    }
}
