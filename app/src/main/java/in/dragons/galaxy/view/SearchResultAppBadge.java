package in.dragons.galaxy.view;

import android.content.Context;
import android.text.TextUtils;

import in.dragons.galaxy.R;
import in.dragons.galaxy.Util;

public class SearchResultAppBadge extends AppBadge {

    @Override
    public void draw() {
        line2.clear();
        line3.clear();
        Context c = view.getContext();
        line2.add(c.getString(R.string.details_size, Util.addSiPrefix((int) app.getSize())));
        line2.add(app.isEarlyAccess() ? c.getString(R.string.early_access) : c.getString(R.string.details_rating, app.getRating().getAverage()));
        line3.add(app.getPrice());
        line3.add(c.getString(app.containsAds() ? R.string.list_app_has_ads : R.string.list_app_no_ads));
        super.draw();
    }
}
