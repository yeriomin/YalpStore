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

package com.github.yeriomin.yalpstore;

import android.view.View;
import android.widget.ListView;

import com.github.yeriomin.yalpstore.view.ListItem;

import java.util.List;

public abstract class ListActivity extends YalpStoreActivity {

    protected ListView listView;

    abstract protected int getLayoutId();

    public ListView getListView() {
        return listView;
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        View emptyView = findViewById(android.R.id.empty);
        listView = findViewById(android.R.id.list);
        if (null == listView) {
            return;
        }
        if (emptyView != null) {
            listView.setEmptyView(emptyView);
        }
        if (null == listView.getAdapter()) {
            listView.setAdapter(new ListAdapter(this, getLayoutId()));
        }
    }

    public void addItems(List<ListItem> items) {
        if (null == listView || null == listView.getAdapter()) {
            return;
        }
        ((ListAdapter) listView.getAdapter()).addAll(items);
    }
}
