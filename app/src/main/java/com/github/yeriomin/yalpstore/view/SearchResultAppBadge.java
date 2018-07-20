/*
 * Yalp Store
 * Copyright (C) 2018 Sergey Yeriomin <yeriomin@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.github.yeriomin.yalpstore.view;

import android.content.Context;
import android.text.TextUtils;

import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.Util;

public class SearchResultAppBadge extends AppBadge {

    @Override
    public void draw() {
        line2.clear();
        line3.clear();
        Context c = view.getContext();
        line2.add(c.getString(R.string.details_installs, Util.addSiPrefix(app.getInstalls())));
        line2.add(app.isEarlyAccess() ? c.getString(R.string.early_access) : c.getString(R.string.details_rating, app.getRating().getAverage()));
        line2.add(TextUtils.isEmpty(app.getUpdated()) ? c.getString(R.string.list_incompatible) : app.getUpdated());
        if (!TextUtils.isEmpty(app.getPrice())) {
            line3.add(app.getPrice());
        }
        line3.add(c.getString(app.containsAds() ? R.string.list_app_has_ads : R.string.list_app_no_ads));
        line3.add(c.getString(app.getDependencies().isEmpty() ? R.string.list_app_independent_from_gsf : R.string.list_app_depends_on_gsf));
        super.draw();
    }
}
