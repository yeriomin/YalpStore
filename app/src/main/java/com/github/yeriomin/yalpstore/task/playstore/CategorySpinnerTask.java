package com.github.yeriomin.yalpstore.task.playstore;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.github.yeriomin.yalpstore.CategoryManager;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.SearchActivity;
import com.github.yeriomin.yalpstore.Util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class CategorySpinnerTask extends CategoryTask {

    @Override
    protected void fill() {
        Spinner filter = ((SearchActivity) context).findViewById(R.id.filter);
        if (filter.getVisibility() == View.VISIBLE) {
            return;
        }
        final Map<String, String> categories = manager.getCategoriesFromSharedPreferences();
        Util.addToStart((LinkedHashMap<String, String>) categories, CategoryManager.TOP, context.getString(R.string.search_filter));
        fill(filter, categories);
    }

    private void fill(Spinner filter, final Map<String, String> categories) {
        filter.setVisibility(View.VISIBLE);
        filter.setAdapter(getAdapter(categories, R.layout.spinner_item));
        filter.setSelection(0);
        filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((SearchActivity) context).setCategoryId(new ArrayList<>(categories.keySet()).get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
