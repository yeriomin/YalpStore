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

package com.github.yeriomin.yalpstore.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.github.yeriomin.yalpstore.CategoryManager;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.Util;
import com.github.yeriomin.yalpstore.YalpStoreActivity;
import com.github.yeriomin.yalpstore.model.Filter;
import com.github.yeriomin.yalpstore.view.DialogWrapper;
import com.github.yeriomin.yalpstore.view.DialogWrapperAbstract;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class FilterMenu {

    static private final String FILTER_SYSTEM_APPS = "FILTER_SYSTEM_APPS";
    static private final String FILTER_APPS_WITH_ADS = "FILTER_APPS_WITH_ADS";
    static private final String FILTER_PAID_APPS = "FILTER_PAID_APPS";
    static private final String FILTER_GSF_DEPENDENT_APPS = "FILTER_GSF_DEPENDENT_APPS";
    static private final String FILTER_CATEGORY = "FILTER_CATEGORY";
    static private final String FILTER_RATING = "FILTER_RATING";
    static private final String FILTER_DOWNLOADS = "FILTER_DOWNLOADS";

    static private final Map<Float, String> ratingLabels = new HashMap<>();
    static private final Map<Integer, String> downloadsLabels = new HashMap<>();

    private YalpStoreActivity activity;

    public FilterMenu(YalpStoreActivity activity) {
        this.activity = activity;
        String[] ratingValues = activity.getResources().getStringArray(R.array.filterRatingValues);
        for (int i = 0; i < ratingValues.length; i++) {
            ratingLabels.put(Float.parseFloat(ratingValues[i]), activity.getResources().getStringArray(R.array.filterRatingLabels)[i]);
        }
        String[] downloadsValues = activity.getResources().getStringArray(R.array.filterDownloadsValues);
        for (int i = 0; i < downloadsValues.length; i++) {
            downloadsLabels.put(Integer.parseInt(downloadsValues[i]), activity.getResources().getStringArray(R.array.filterDownloadsLabels)[i]);
        }
    }

    public Filter getFilterPreferences() {
        Filter filter = new Filter();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        filter.setSystemApps(prefs.getBoolean(FILTER_SYSTEM_APPS, false));
        filter.setAppsWithAds(prefs.getBoolean(FILTER_APPS_WITH_ADS, true));
        filter.setPaidApps(prefs.getBoolean(FILTER_PAID_APPS, true));
        filter.setGsfDependentApps(prefs.getBoolean(FILTER_GSF_DEPENDENT_APPS, true));
        filter.setCategory(prefs.getString(FILTER_CATEGORY, CategoryManager.TOP));
        filter.setRating(prefs.getFloat(FILTER_RATING, 0.0f));
        filter.setDownloads(prefs.getInt(FILTER_DOWNLOADS, 0));
        return filter;
    }

    public void onCreateOptionsMenu(Menu menu) {
        Filter filter = getFilterPreferences();
        menu.findItem(R.id.filter_system_apps).setChecked(filter.isSystemApps());
        menu.findItem(R.id.filter_apps_with_ads).setChecked(filter.isAppsWithAds());
        menu.findItem(R.id.filter_paid_apps).setChecked(filter.isPaidApps());
        menu.findItem(R.id.filter_gsf_dependent_apps).setChecked(filter.isGsfDependentApps());
        menu.findItem(R.id.filter_category).setTitle(activity.getString(
            R.string.action_filter_category,
            new CategoryManager(activity).getCategoryName(filter.getCategory())
        ));
        menu.findItem(R.id.filter_rating).setTitle(activity.getString(
            R.string.action_filter_rating,
            ratingLabels.get(filter.getRating())
        ));
        menu.findItem(R.id.filter_downloads).setTitle(activity.getString(
            R.string.action_filter_downloads,
            downloadsLabels.get(filter.getDownloads())
        ));
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter_system_apps:
                putBoolean(FILTER_SYSTEM_APPS, !item.isChecked());
                break;
            case R.id.filter_apps_with_ads:
                putBoolean(FILTER_APPS_WITH_ADS, !item.isChecked());
                break;
            case R.id.filter_paid_apps:
                putBoolean(FILTER_PAID_APPS, !item.isChecked());
                break;
            case R.id.filter_gsf_dependent_apps:
                putBoolean(FILTER_GSF_DEPENDENT_APPS, !item.isChecked());
                break;
            case R.id.filter_category:
                getCategoryDialog().show();
                break;
            case R.id.filter_rating:
                getRatingDialog().show();
                break;
            case R.id.filter_downloads:
                getDownloadsDialog().show();
                break;
        }
        return true;
    }

    private void putBoolean(String key, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(activity).edit().putBoolean(key, value).commit();
        restartActivity();
    }

    private void restartActivity() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            activity.recreate();
        } else {
            Intent intent = activity.getIntent();
            activity.finish();
            activity.startActivity(intent);
        }
    }

    private DialogWrapperAbstract getCategoryDialog() {
        final Map<String, String> categories = new CategoryManager(activity).getCategoriesFromSharedPreferences();
        Util.addToStart((LinkedHashMap<String, String>) categories, CategoryManager.TOP, activity.getString(R.string.search_filter));
        return getDialog(
            categories.values().toArray(new String[categories.size()]),
            new ConfirmOnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    PreferenceManager.getDefaultSharedPreferences(activity).edit().putString(
                        FILTER_CATEGORY,
                        categories.keySet().toArray(new String[categories.size()])[which]
                    ).commit();
                    super.onClick(dialog, which);
                }
            }
        );
    }

    private DialogWrapperAbstract getRatingDialog() {
        return getDialog(
            activity.getResources().getStringArray(R.array.filterRatingLabels),
            new ConfirmOnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    PreferenceManager.getDefaultSharedPreferences(activity).edit().putFloat(
                        FILTER_RATING,
                        Float.parseFloat(activity.getResources().getStringArray(R.array.filterRatingValues)[which])
                    ).commit();
                    super.onClick(dialog, which);
                }
            }
        );
    }

    private DialogWrapperAbstract getDownloadsDialog() {
        return getDialog(
            activity.getResources().getStringArray(R.array.filterDownloadsLabels),
            new ConfirmOnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    PreferenceManager.getDefaultSharedPreferences(activity).edit().putInt(
                        FILTER_DOWNLOADS,
                        Integer.parseInt(activity.getResources().getStringArray(R.array.filterDownloadsValues)[which])
                    ).commit();
                    super.onClick(dialog, which);
                }
            }
        );
    }

    private DialogWrapperAbstract getDialog(String[] labels, ConfirmOnClickListener listener) {
        return new DialogWrapper(activity)
            .setAdapter(
                new ArrayAdapter<>(activity, android.R.layout.select_dialog_item, labels),
                listener
            )
            .create()
        ;
    }

    private class ConfirmOnClickListener implements DialogInterface.OnClickListener {

        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            restartActivity();
        }
    }
}
