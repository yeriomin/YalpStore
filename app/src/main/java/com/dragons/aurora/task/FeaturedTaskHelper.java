package com.dragons.aurora.task;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.animation.AnimationUtils;

import com.dragons.aurora.R;
import com.dragons.aurora.adapters.FeaturedAppsAdapter;
import com.dragons.aurora.adapters.RecyclerAppsAdapter;
import com.dragons.aurora.model.App;

import java.util.List;

public class FeaturedTaskHelper extends CategoryTaskHelper {

    private Context context;
    private RecyclerView recyclerView;

    public FeaturedTaskHelper(Context context, RecyclerView recyclerView) {
        super(context, recyclerView);
        this.context = context;
        this.recyclerView = recyclerView;
    }

    @Override
    public void setupListView(RecyclerView recyclerView, List<App> appsToAdd) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(context, R.anim.layout_anim));
        recyclerView.setAdapter(new FeaturedAppsAdapter(context, appsToAdd));
    }
}
