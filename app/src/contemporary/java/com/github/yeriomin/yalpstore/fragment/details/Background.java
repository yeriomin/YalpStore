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

import android.support.design.widget.CollapsingToolbarLayout;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.yeriomin.yalpstore.DetailsActivity;
import com.github.yeriomin.yalpstore.NetworkUtil;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.fragment.Abstract;
import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.task.LoadImageTask;

public class Background extends Abstract {

    private static final int DEFAULT_WIDTH = 512;
    private static final int DEFAULT_HEIGHT = 250;

    private int smallerDimension;

    public Background(DetailsActivity activity, App app) {
        super(activity, app);
        DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
        smallerDimension = Math.min(metrics.widthPixels, metrics.heightPixels);
    }

    @Override
    public void draw() {
        View background = activity.findViewById(R.id.background);
        CollapsingToolbarLayout collapsingToolbarLayout = activity.findViewById(R.id.collapsing_toolbar_layout);
        collapsingToolbarLayout.setExpandedTitleColor(activity.getResources().getColor(android.R.color.transparent));
        if (!NetworkUtil.isNetworkAvailable(activity) || (!app.isInPlayStore() && !TextUtils.isEmpty(app.getDeveloperName()) && null == app.getPageBackgroundImage())) {
            collapsingToolbarLayout.setTitleEnabled(false);
            collapsingToolbarLayout.getLayoutParams().height = CollapsingToolbarLayout.LayoutParams.MATCH_PARENT;
            background.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            background.setVisibility(View.GONE);
        } else {
            int height = DEFAULT_HEIGHT*smallerDimension/DEFAULT_WIDTH;
            collapsingToolbarLayout.setTitleEnabled(true);
            collapsingToolbarLayout.getLayoutParams().height = height;
            background.getLayoutParams().height = height;
            background.setVisibility(View.VISIBLE);
            if (null != app.getPageBackgroundImage()) {
                new LoadImageTask((ImageView) background)
                    .setPlaceholder(false)
                    .setFadeInMillis(500)
                    .setImageSource(app.getPageBackgroundImage())
                    .executeOnExecutorIfPossible()
                ;
            }
        }
    }
}
