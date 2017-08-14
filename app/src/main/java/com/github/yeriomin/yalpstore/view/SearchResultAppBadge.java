package com.github.yeriomin.yalpstore.view;

import android.content.Context;
import android.text.TextUtils;

import com.github.yeriomin.yalpstore.R;

public class SearchResultAppBadge extends AppBadge {

    @Override
    public void draw() {
        line2.clear();
        line3.clear();
        Context c = view.getContext();
        line2.add(c.getString(R.string.details_installs, app.getInstalls()));
        line2.add(app.isEarlyAccess() ? c.getString(R.string.early_access) : c.getString(R.string.details_rating, app.getRating().getAverage()));
        line2.add(TextUtils.isEmpty(app.getUpdated()) ? c.getString(R.string.list_incompatible) : app.getUpdated());
        line3.add(app.getPrice());
        line3.add(c.getString(app.containsAds() ? R.string.list_app_has_ads : R.string.list_app_no_ads));
        line3.add(c.getString(app.getDependencies().isEmpty() ? R.string.list_app_independent_from_gsf : R.string.list_app_depends_on_gsf));
        super.draw();
    }
}
