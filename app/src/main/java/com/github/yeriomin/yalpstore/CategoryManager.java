package com.github.yeriomin.yalpstore;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class CategoryManager {

    public static final String TOP = "0_CATEGORY_TOP";
    private static final String DELIMITER = ",";

    private Activity activity;
    private SharedPreferencesTranslator translator;
    private SharedPreferences prefs;

    public CategoryManager(Activity activity) {
        this.activity = activity;
        prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        translator = new SharedPreferencesTranslator(prefs);
    }

    public String getCategoryName(String categoryId) {
        return translator.getString(categoryId);
    }

    public void downloadCategoryNames() {
        if (needToDownload()) {
            getTask().execute();
        }
    }

    public void save(String parent, Map<String, String> categories) {
        putCategorySet(parent, categories.keySet());
        for (String categoryId: categories.keySet()) {
            translator.putString(categoryId, categories.get(categoryId));
        }
    }

    public void fill(ListView list) {
        final Map<String, String> categories = getCategoriesFromSharedPreferences();
        list.setAdapter(getAdapter(getCategoriesFromSharedPreferences(), android.R.layout.simple_list_item_1));
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CategoryAppsActivity.start(activity, new ArrayList<>(categories.keySet()).get(position));
            }
        });
    }

    public void fill(Spinner filter) {
        final Map<String, String> categories = getCategoriesFromSharedPreferences();
        filter.setVisibility(View.VISIBLE);
        filter.setAdapter(getAdapter(getCategoriesFromSharedPreferences(), R.layout.spinner_item));
        filter.setSelection(0);
        filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((SearchResultActivity) activity).setCategoryId(new ArrayList<>(categories.keySet()).get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public boolean fits(String appCategoryId, String chosenCategoryId) {
        return null == chosenCategoryId
            || chosenCategoryId.equals(TOP)
            || appCategoryId.equals(chosenCategoryId)
            || getCategorySet(chosenCategoryId).contains(appCategoryId)
            ;
    }

    private ArrayAdapter getAdapter(Map<String, String> categories, int itemLayoutId) {
        return new ArrayAdapter<>(
            activity,
            itemLayoutId,
            new ArrayList<>(categories.values())
        );
    }

    private boolean needToDownload() {
        Set<String> topSet = getCategorySet(TOP);
        if (topSet.isEmpty()) {
            return true;
        }
        int size = topSet.size();
        String categoryId = topSet.toArray(new String[size])[size - 1];
        return translator.getString(categoryId).equals(categoryId);
    }

    private CategoryTask getTask() {
        CategoryTask task = new CategoryTask();
        task.setManager(this);
        task.setContext(activity);
        return task;
    }

    private Map<String, String> getCategoriesFromSharedPreferences() {
        Map<String, String> categories = new TreeMap<>();
        categories.put(TOP, activity.getString(R.string.search_filter));
        Set<String> topSet = getCategorySet(TOP);
        for (String topCategoryId: topSet) {
            categories.put(topCategoryId, translator.getString(topCategoryId));
            Set<String> subSet = getCategorySet(topCategoryId);
            for (String subCategoryId: subSet) {
                categories.put(subCategoryId, categories.get(topCategoryId) + " - " + translator.getString(subCategoryId));
            }
        }
        return categories;
    }

    private Set<String> getCategorySet(String parent) {
        return new HashSet<>(Arrays.asList(TextUtils.split(prefs.getString(parent, ""), DELIMITER)));
    }

    private void putCategorySet(String parent, Set<String> categories) {
        prefs.edit().putString(parent, TextUtils.join(DELIMITER, categories)).apply();
    }
}
