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

import android.support.v7.widget.SearchView;
import android.view.Menu;

public class SearchActivity extends SearchActivityAbstract {

    @Override
    protected void search(String query, boolean isPackageName) {
        if (isPackageName) {
            startActivity(DetailsActivity.getDetailsIntent(this, query));
            finish();
        } else {
            onNewIntent(getSearchIntent(query));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        ((SearchView) menu.findItem(R.id.action_search).getActionView()).setQuery(query,false);
        return result;
    }
}
