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
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.task.playstore.UserProfileTask;

import java.lang.ref.WeakReference;

import static com.github.yeriomin.yalpstore.PlayStoreApiAuthenticator.PREFERENCE_APP_PROVIDED_EMAIL;
import static com.github.yeriomin.yalpstore.PlayStoreApiAuthenticator.PREFERENCE_EMAIL;

public abstract class BaseActivity extends AppCompatActivity {

    private static final int WRAPPER_LAYOUT_ID = R.layout.base_activity_layout;

    private static SearchSuggestionTask previousSearchSuggestTask;

    protected int wrapperLayoutResId = WRAPPER_LAYOUT_ID;

    private SimpleCursorAdapter suggestionsAdapter;
    private ActionBarDrawerToggle drawerToggle;

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
                previousSearchSuggestTask = (SearchSuggestionTask) new SearchSuggestionTask(BaseActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, s);
                return false;
            }
        });
        return result;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
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

    private void initToolbar() {
        final DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };
        drawerToggle.syncState();
        drawerLayout.addDrawerListener(drawerToggle);


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
                    drawerLayout.closeDrawers();
                    return true;
                }
            }
        );
        redrawLogoutItem();
    }

    public void redrawLogoutItem(){
        NavigationView navigationView = findViewById(R.id.nav_view);
        String email = PreferenceUtil.getString(this, PREFERENCE_EMAIL);
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.username)).setText(email.split("@")[0]);
        if (!TextUtils.isEmpty(email)) {
            navigationView.getMenu().findItem(R.id.action_logout).setVisible(true);
            if (!PreferenceUtil.getBoolean(this, PREFERENCE_APP_PROVIDED_EMAIL)) {
                new UserProfileTask((ImageView) navigationView.getHeaderView(0).findViewById(R.id.avatar)).execute();
            }
        }
    }

    static private class SearchSuggestionTask extends AsyncTask<String, Void, Cursor> {

        private String requestString;
        private WeakReference<BaseActivity> activityRef;

        public SearchSuggestionTask(BaseActivity activity) {
            this.activityRef = new WeakReference<>(activity);
        }

        public String getRequestString() {
            return requestString;
        }

        @Override
        protected Cursor doInBackground(String... strings) {
            if (null == activityRef.get() || isCancelled()) {
                return null;
            }
            requestString = strings[0];
            return activityRef.get().getContentResolver().query(new Uri.Builder().scheme("content").authority(BuildConfig.APPLICATION_ID + ".YalpStoreSuggestionProvider").appendEncodedPath(requestString).build(), null, null, null, null);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (null == activityRef.get() || isCancelled()) {
                cursor.close();
                return;
            }
            activityRef.get().showSuggestions(cursor);
            previousSearchSuggestTask = null;
        }
    }
}
