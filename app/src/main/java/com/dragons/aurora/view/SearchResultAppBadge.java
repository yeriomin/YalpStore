package com.dragons.aurora.view;

import android.content.Context;
import android.widget.ImageView;

import com.dragons.aurora.R;
import com.dragons.aurora.Util;

public class SearchResultAppBadge extends AppBadge {

    @Override
    public void draw() {
        line2.clear();
        line3.clear();
        Context c = view.getContext();
        line2.add(c.getString(R.string.details_size, Util.addSiPrefix((int) app.getSize())));
        if(!app.isEarlyAccess())
            line2.add(c.getString(R.string.details_rating, (app.getRating().getAverage()))+" ★");
        line3.add(app.getPrice());
        line3.add(c.getString(app.containsAds() ? R.string.list_app_has_ads : R.string.list_app_no_ads));
        line3.add(c.getString(app.getDependencies().isEmpty() ? R.string.list_app_independent_from_gsf : R.string.list_app_depends_on_gsf));
        drawIcon((ImageView) view.findViewById(R.id.icon));
        super.draw();
    }
}
