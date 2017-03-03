package com.github.yeriomin.yalpstore;

import android.os.Bundle;
import android.widget.ListView;

public class CategoryListActivity extends YalpStoreActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categories_activity_layout);
        setTitle(getString(R.string.action_categories));
        new CategoryManager(this).fill((ListView) findViewById(android.R.id.list));
    }
}
