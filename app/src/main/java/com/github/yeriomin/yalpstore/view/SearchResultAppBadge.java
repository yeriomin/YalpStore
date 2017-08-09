package com.github.yeriomin.yalpstore.view;

import android.content.Context;
import android.text.TextUtils;

import com.github.yeriomin.yalpstore.R;

public class SearchResultAppBadge extends AppBadge {

    @Override
    public void draw() {
        super.draw();
        Context c = view.getContext();
        String installs = c.getString(R.string.details_installs, app.getInstalls());
        String rating = app.isEarlyAccess() ? c.getString(R.string.early_access) : c.getString(R.string.details_rating, app.getRating().getAverage());
        String updated = TextUtils.isEmpty(app.getUpdated()) ? c.getString(R.string.list_incompatible) : app.getUpdated();
        setText(R.id.text2, c.getString(R.string.list_line_3_search, installs, rating, updated));
        String ads = c.getString(app.containsAds() ? R.string.list_app_has_ads : R.string.list_app_no_ads);
        String gsf = c.getString(app.getDependencies().isEmpty() ? R.string.list_app_independent_from_gsf : R.string.list_app_depends_on_gsf);
        setText(R.id.text3, c.getString(R.string.list_line_3_search, app.getPrice(), ads, gsf));
    }
}
