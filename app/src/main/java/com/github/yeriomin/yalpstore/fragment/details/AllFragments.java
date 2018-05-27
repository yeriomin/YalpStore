/*
 * Yalp Store
 * Copyright (C) 2018 Sergey Yeriomin <yeriomin@gmail.com>
 *
 * activity program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * activity program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with activity program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.github.yeriomin.yalpstore.fragment.details;

import com.github.yeriomin.yalpstore.DetailsActivity;
import com.github.yeriomin.yalpstore.fragment.Abstract;
import com.github.yeriomin.yalpstore.model.App;

public class AllFragments extends Abstract {
    
    @Override
    public void draw() {
        new Background((DetailsActivity) activity, app).draw();
        new GeneralDetails((DetailsActivity) activity, app).draw();
        new Wishlist(activity, app).draw();
        new Permissions(activity, app).draw();
        new Screenshot((DetailsActivity) activity, app).draw();
        new Review((DetailsActivity) activity, app).draw();
        new AppLists((DetailsActivity) activity, app).draw();
        new BackToPlayStore((DetailsActivity) activity, app).draw();
        new Share((DetailsActivity) activity, app).draw();
        new SystemAppPage((DetailsActivity) activity, app).draw();
        new Video((DetailsActivity) activity, app).draw();
        new Beta((DetailsActivity) activity, app).draw();
        new Exodus(activity, app).draw();
        new Fdroid(activity, app).draw();
        new InstantAppLink(activity, app).draw();
    }

    public AllFragments(DetailsActivity activity, App app) {
        super(activity, app);
    }
}
