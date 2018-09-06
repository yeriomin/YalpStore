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

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import com.github.yeriomin.yalpstore.model.LoginInfo;
import com.github.yeriomin.yalpstore.view.DialogWrapperAbstract;

import java.util.List;


public abstract class BaseActivity extends Activity {

    abstract protected DialogWrapperAbstract showLogOutDialog();
    abstract protected void fillAccountList(Menu menu, List<LoginInfo> users);
    abstract protected List<LoginInfo> getUsers();

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!YalpStoreApplication.user.isLoggedIn()) {
            menu.findItem(R.id.action_logout).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            addQueryTextListener(menu.findItem(R.id.action_search));
        }
        fillAccountList(menu, getUsers());
        return super.onCreateOptionsMenu(menu);
    }

    protected void markCurrentAccount(MenuItem item) {
        item
            .setCheckable(true)
            .setChecked(true)
        ;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void addQueryTextListener(MenuItem searchItem) {
        SearchView searchView = (SearchView) searchItem.getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (null != searchManager) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setQueryHint(getString(R.string.search_title));
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent i = new Intent(BaseActivity.this, SearchActivity.class);
                i.setAction(Intent.ACTION_SEARCH);
                i.putExtra(SearchManager.QUERY, query);
                startActivity(i);
                return false;
            }
        });
    }

    public void setContentViewNoWrapper(int layoutResID) {
        super.setContentView(layoutResID);
    }

    public void redrawAccounts() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            invalidateOptionsMenu();
        }
    }
}
