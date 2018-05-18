package com.dragons.aurora.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dragons.aurora.CategoryManager;
import com.dragons.aurora.R;
import com.dragons.aurora.adapters.CategoryFilterAdapter;
import com.dragons.aurora.task.playstore.CategoryAppsTask;
import com.dragons.aurora.view.AdaptiveToolbar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoryAppsFragment extends CategoryAppsTask {

    public static String categoryId;

    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.category_tabs)
    TabLayout tabLayout;
    @BindView(R.id.adaptive_toolbar)
    AdaptiveToolbar adaptiveToolbar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_endless, container, false);

        ButterKnife.bind(this, view);

        adaptiveToolbar.getAction_icon().setOnClickListener((v -> this.getActivity().onBackPressed()));
        adaptiveToolbar.getTitle0().setText(new CategoryManager(getContext()).getCategoryName(categoryId));
        adaptiveToolbar.getTitle1().setVisibility(View.GONE);

        viewPager.setAdapter(new CategoryFilterAdapter(getActivity(), getActivity().getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null)
            categoryId = arguments.getString("CategoryId");
        else
            Log.e(this.getClass().getName(), "No category id provided");
    }
}