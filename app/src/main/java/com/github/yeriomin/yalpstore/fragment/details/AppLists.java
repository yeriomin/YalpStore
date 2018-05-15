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

import android.app.SearchManager;
import android.content.Intent;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.ClusterActivity;
import com.github.yeriomin.yalpstore.DetailsActivity;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.SearchActivity;
import com.github.yeriomin.yalpstore.SearchActivityAbstract;
import com.github.yeriomin.yalpstore.Util;
import com.github.yeriomin.yalpstore.fragment.Abstract;
import com.github.yeriomin.yalpstore.model.App;

public class AppLists extends Abstract {

    public AppLists(DetailsActivity activity, App app) {
        super(activity, app);
    }

    @Override
    public void draw() {
        LinearLayout relatedLinksLayout = activity.findViewById(R.id.related_links);
        boolean developerLinkFound = false;
        relatedLinksLayout.removeAllViews();
        for (final String label: app.getRelatedLinks().keySet()) {
            relatedLinksLayout.setVisibility(View.VISIBLE);
            relatedLinksLayout.addView(buildLinkView(label, app.getRelatedLinks().get(label)));
            if (label.contains(app.getDeveloperName())) {
                developerLinkFound = true;
            }
        }
        if (!developerLinkFound && !TextUtils.isEmpty(app.getDeveloperName())) {
            addAppsByThisDeveloper();
        }
    }

    private TextView buildLinkView(final String label, final String url) {
        TextView linkView = new TextView(activity);
        linkView.setHeight(Util.getPx(activity, 48));
        linkView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_link, 0, 0, 0);
        linkView.setCompoundDrawablePadding(Util.getPx(activity, 6));
        linkView.setText(label);
        linkView.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
        linkView.setPadding(Util.getPx(activity, 16), 0, Util.getPx(activity, 16), 0);
        linkView.setGravity(Gravity.CENTER_VERTICAL);
        linkView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClusterActivity.start(activity, url, label);
            }
        });
        return linkView;
    }

    private void addAppsByThisDeveloper() {
        TextView textView = activity.findViewById(R.id.apps_by_same_developer);
        textView.setText(activity.getString(R.string.apps_by, app.getDeveloperName()));
        textView.setVisibility(View.VISIBLE);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, SearchActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setAction(Intent.ACTION_SEARCH);
                intent.putExtra(SearchManager.QUERY, SearchActivityAbstract.PUB_PREFIX + app.getDeveloperName());
                activity.startActivity(intent);
            }
        });
    }
}
