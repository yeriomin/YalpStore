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

import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.model.Event;

public class HistoryItem extends ListItem {

    private Event event;

    public void setEvent(Event event) {
        this.event = event;
    }

    @Override
    public void draw() {
        view.findViewById(R.id.app).setVisibility(event.getType().equals(Event.TYPE.BACKGROUND_UPDATE_CHECK) ? View.GONE : View.VISIBLE);
        view.findViewById(R.id.icon).setVisibility(event.getType().equals(Event.TYPE.BACKGROUND_UPDATE_CHECK) ? View.GONE : View.VISIBLE);
        view.findViewById(R.id.message).setVisibility(null == event ? View.GONE : View.VISIBLE);
        view.findViewById(R.id.date).setVisibility(null == event ? View.GONE : View.VISIBLE);
        view.findViewById(R.id.changes).setVisibility((null == event || !event.getType().equals(Event.TYPE.UPDATE)) ? View.GONE : View.VISIBLE);
        drawApp();
        if (null != event) {
            drawDbItem();
        }
    }

    private void drawApp() {
        view.setFocusable(true);
        if (null == app) {
            ((TextView) view.findViewById(R.id.app)).setText(event.getPackageName());
            ((ImageView) view.findViewById(R.id.icon)).setImageResource(R.drawable.ic_placeholder);
        } else {
            ((TextView) view.findViewById(R.id.app)).setText(TextUtils.isEmpty(app.getDisplayName()) ? app.getPackageName() : app.getDisplayName());
            if (null == app.getIconInfo().getApplicationInfo()) {
                ((ImageView) view.findViewById(R.id.icon)).setImageResource(R.drawable.ic_placeholder);
            } else {
                view.setFocusable(false);
                drawIcon((ImageView) view.findViewById(R.id.icon), app.getPackageName(), app.getIconInfo());
            }
        }
    }

    private void drawDbItem() {
        ((TextView) view.findViewById(R.id.message)).setText(event.getMessage());
        ((TextView) view.findViewById(R.id.date)).setText(android.text.format.DateUtils.getRelativeTimeSpanString(event.getTime()));
        if (event.getType().equals(Event.TYPE.UPDATE)) {
            ((TextView) view.findViewById(R.id.changes)).setText(TextUtils.isEmpty(event.getChanges()) ? view.getContext().getString(R.string.events_no_changes) : Html.fromHtml(event.getChanges()).toString());
        }
    }
}
