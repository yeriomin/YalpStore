package com.dragons.aurora.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.dragons.aurora.R;
import com.dragons.aurora.playstoreapiv2.GooglePlayAPI;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TopTrendingApps extends TopFreeApps {
    @BindView(R.id.endless_apps_list)
    RecyclerView recyclerView;
    @BindView(R.id.unicorn)
    RelativeLayout unicorn;
    @BindView(R.id.ohhSnap)
    RelativeLayout ohhSnap;
    @BindView(R.id.progress)
    RelativeLayout progress;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.app_endless_inc, container, false);
        ButterKnife.bind(this, view);
        setIterator(setupIterator(CategoryAppsFragment.categoryId, GooglePlayAPI.SUBCATEGORY.MOVERS_SHAKERS));
        setRecyclerView(recyclerView);
        fetchCategoryApps(false);
        Button ohhSnap_retry = view.findViewById(R.id.ohhSnap_retry);
        ohhSnap_retry.setOnClickListener(click -> {
            if (isLoggedIn() && isConnected(getContext())) {
                hide(view, R.id.ohhSnap);
                fetchCategoryApps(false);
            }
        });
        Button retry_query = view.findViewById(R.id.recheck_query);
        retry_query.setOnClickListener(click -> {
            if (isLoggedIn() && isConnected(getContext())) {
                hide(view, R.id.unicorn);
                fetchCategoryApps(false);
            }
        });
        return view;
    }
}