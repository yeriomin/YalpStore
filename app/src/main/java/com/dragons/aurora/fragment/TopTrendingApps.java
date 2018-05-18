package com.dragons.aurora.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dragons.aurora.R;
import com.dragons.aurora.playstoreapiv2.GooglePlayAPI;

public class TopTrendingApps extends TopFreeApps {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.app_endless_inc, container, false);
        setIterator(setupIterator(CategoryAppsFragment.categoryId, GooglePlayAPI.SUBCATEGORY.MOVERS_SHAKERS));
        setRecyclerView(view.findViewById(R.id.endless_apps_list));
        fetchCategoryApps(false);
        return view;
    }
}