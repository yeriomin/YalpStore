package com.github.yeriomin.yalpstore;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import static com.github.yeriomin.yalpstore.PlayStoreApiAuthenticator.PREFERENCE_EMAIL;

public abstract class BaseActivity extends Activity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            addQueryTextListener(menu.findItem(R.id.action_search));
        }
        if (!TextUtils.isEmpty(PreferenceUtil.getString(this, PREFERENCE_EMAIL))) {
            menu.findItem(R.id.action_logout).setVisible(true);
        }
        return super.onCreateOptionsMenu(menu);
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
}
