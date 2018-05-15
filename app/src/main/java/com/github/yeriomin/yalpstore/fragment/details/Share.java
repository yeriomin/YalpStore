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
import android.view.View;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.DetailsActivity;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.fragment.Abstract;
import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.view.IntentOnClickListener;
import com.github.yeriomin.yalpstore.view.PurchaseDialogBuilder;

public class Share extends Abstract {

    public Share(DetailsActivity activity, App app) {
        super(activity, app);
    }

    @Override
    public void draw() {
        TextView shareView = activity.findViewById(R.id.share);
        if (null == shareView) {
            return;
        }
        if (null == app) {
            shareView.setVisibility(View.GONE);
            return;
        }
        shareView.setVisibility(View.VISIBLE);
        shareView.setOnClickListener(new IntentOnClickListener(activity) {
            @Override
            protected Intent buildIntent() {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, app.getDisplayName());
                i.putExtra(Intent.EXTRA_TEXT, PurchaseDialogBuilder.URL_PURCHASE + app.getPackageName());
                return Intent.createChooser(i, activity.getString(R.string.details_share));
            }
        });
    }
}
