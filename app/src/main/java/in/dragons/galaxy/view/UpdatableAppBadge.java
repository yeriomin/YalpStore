package in.dragons.galaxy.view;

import android.content.Context;
import android.text.TextUtils;

import in.dragons.galaxy.R;

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
        super.draw();
    }
}
