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

package com.github.yeriomin.yalpstore.fragment.details;

import android.content.Intent;

import com.github.yeriomin.yalpstore.DetailsActivity;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.view.IntentOnClickListener;

public class Share extends Abstract {

    static private String PLAYSTORE_LINK_PREFIX= "https://play.google.com/store/apps/details?id=";

    public Share(DetailsActivity activity, App app) {
        super(activity, app);
    }

    @Override
    public void draw() {
        activity.findViewById(R.id.share).setOnClickListener(new IntentOnClickListener(activity) {
            @Override
            protected Intent buildIntent() {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, app.getDisplayName());
                i.putExtra(Intent.EXTRA_TEXT, PLAYSTORE_LINK_PREFIX + app.getPackageName());
                return Intent.createChooser(i, activity.getString(R.string.details_share));
            }
        });
    }
}
