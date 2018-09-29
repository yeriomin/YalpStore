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

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.model.LoginInfo;
import com.github.yeriomin.yalpstore.task.playstore.NavHeaderUpdateTask;
import com.github.yeriomin.yalpstore.task.playstore.SearchSuggestionTask;
import com.github.yeriomin.yalpstore.view.DialogWrapperAbstract;

import java.lang.reflect.Field;
import java.util.List;

public abstract class BaseActivity extends AppCompatActivity {

    private static final int WRAPPER_LAYOUT_ID = R.layout.base_activity_layout;

    private static SearchSuggestionTask previousSearchSuggestTask;

    protected int wrapperLayoutResId = WRAPPER_LAYOUT_ID;

    private SimpleCursorAdapter suggestionsAdapter;

    abstract protected DialogWrapperAbstract showLogOutDialog();
    abstract protected void fillAccountList(Menu menu, List<LoginInfo> users);
    abstract protected List<LoginInfo> getUsers();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSubmitButtonEnabled(true);
        suggestionsAdapter = new SimpleCursorAdapter(
            this,
            R.layout.suggestion_list_item,
            null,
            new String[] {
                SearchManager.SUGGEST_COLUMN_TEXT_1,
                SearchManager.SUGGEST_COLUMN_ICON_1
            },
            new int[] {R.id.text, R.id.icon}
        );
        searchView.setSuggestionsAdapter(suggestionsAdapter);
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {

            @Override
            public boolean onSuggestionClick(int position) {
                triggerSearch((Cursor) suggestionsAdapter.getItem(position));
                return true;
            }

            @Override
            public boolean onSuggestionSelect(int position) {
                triggerSearch((Cursor) suggestionsAdapter.getItem(position));
                return true;
            }

            private void triggerSearch(Cursor cursor) {
                String data = cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_INTENT_DATA));
                String label = cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));
                search(data, !data.equals(label));
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                search(s, false);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (TextUtils.isEmpty(s)
                    || (null != previousSearchSuggestTask
                        && !TextUtils.isEmpty(previousSearchSuggestTask.getRequestString())
                        && previousSearchSuggestTask.getRequestString().equals(s)
                    )
                ) {
                    return false;
                }
                if (null != previousSearchSuggestTask) {
                    previousSearchSuggestTask.cancel(true);
                }
                previousSearchSuggestTask = (SearchSuggestionTask) new SearchSuggestionTask(BaseActivity.this).execute(s);
                return false;
            }
        });
        return result;
    }

    @Override
    protected void onResume() {
        super.onResume();
        NavigationView navigationView = findViewById(R.id.nav_view);
        if (null == navigationView) {
            return;
        }
        TextView userNameView = navigationView.getHeaderView(0).findViewById(R.id.username);
        TextView deviceView = navigationView.getHeaderView(0).findViewById(R.id.device);
        if (null == userNameView || null == deviceView) {
            return;
        }
        String currentUserName = null == YalpStoreApplication.user.getUserName()
            ? ""
            : YalpStoreApplication.user.getUserName()
        ;
        String currentDeviceName = null == YalpStoreApplication.user.getDeviceDefinitionDisplayName()
            ? ""
            : YalpStoreApplication.user.getDeviceDefinitionDisplayName()
        ;
        if (!currentUserName.equals(userNameView.getText().toString())
            || !currentDeviceName.equals(deviceView.getText().toString())
        ) {
            redrawAccounts();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != suggestionsAdapter && null != suggestionsAdapter.getCursor()) {
            suggestionsAdapter.getCursor().close();
        }
    }

    public void showSuggestions(Cursor cursor) {
        suggestionsAdapter.changeCursor(cursor);
        suggestionsAdapter.notifyDataSetChanged();
        BaseActivity.previousSearchSuggestTask = null;
    }

    protected void search(String query, boolean isPackageName) {
        startActivity(isPackageName ? DetailsActivity.getDetailsIntent(this, query) : getSearchIntent(query));
    }

    protected Intent getSearchIntent(String query) {
        Intent intent = new Intent(this, SearchActivity.class);
        intent.setAction(Intent.ACTION_SEARCH);
        intent.putExtra(SearchManager.QUERY, query);
        return intent;
    }

    public void setContentViewNoWrapper(int layoutResID) {
        super.setContentView(layoutResID);
        initToolbar();
    }

    @Override
    public void setContentView(int layoutResID) {
        new ThemeManager().setTheme(this);
        super.setContentView(wrapperLayoutResId);
        ViewGroup container = findViewById(R.id.specific_layout_container);
        View specificLayout = getLayoutInflater().inflate(layoutResID, container, false);
        container.addView(specificLayout);
        initToolbar();
        onContentChanged();
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        ((Toolbar) findViewById(R.id.toolbar)).setTitle(title);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            ((DrawerLayout) findViewById(R.id.drawer_layout)).openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initToolbar() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        ((NavigationView) findViewById(R.id.nav_view)).setNavigationItemSelectedListener(
            new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {
                    onOptionsItemSelected(menuItem);
                    ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawers();
                    return true;
                }
            }
        );
        redrawAccounts();
    }

    public void redrawAccounts(){
        NavigationView navigationView = findViewById(R.id.nav_view);
        ImageView accountsView = navigationView.getHeaderView(0).findViewById(R.id.accounts);
        final List<LoginInfo> users = getUsers();
        if (YalpStoreApplication.user.isLoggedIn() || !users.isEmpty()) {
            NavHeaderUpdateTask task = new NavHeaderUpdateTask();
            task.setAvatarView((ImageView) navigationView.getHeaderView(0).findViewById(R.id.avatar));
            task.setUserNameView((TextView) navigationView.getHeaderView(0).findViewById(R.id.username));
            task.setDeviceView((TextView) navigationView.getHeaderView(0).findViewById(R.id.device));
            task.setContext(getApplicationContext());
            task.execute();
            accountsView.setVisibility(View.VISIBLE);
            accountsView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAccountsMenu(v, users);
                }
            });
        } else {
            accountsView.setVisibility(View.GONE);
        }
    }

    public void showAccountsMenu(View v, List<LoginInfo> users) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return BaseActivity.this.onOptionsItemSelected(item);
            }
        });
        popup.inflate(R.menu.menu_accounts);
        setForceShowIcon(popup);
        fillAccountList(popup.getMenu(), users);
        popup.show();
    }

    protected void markCurrentAccount(MenuItem item) {
        item.setIcon(R.drawable.ic_check);
    }

    public static void setForceShowIcon(PopupMenu popupMenu) {
        try {
            for (Field field: popupMenu.getClass().getDeclaredFields()) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popupMenu);
                    Class.forName(menuPopupHelper.getClass().getName()).getMethod("setForceShowIcon", boolean.class).invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            // ReflectiveOperationException is not available on older androids, so catching Exception
            e.printStackTrace();
        }
    }
}
