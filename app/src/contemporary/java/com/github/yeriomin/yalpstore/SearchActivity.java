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
