package com.dragons.aurora.adapters;

import android.content.Context;
import android.support.annotation.NonNull;

import com.dragons.aurora.activities.DetailsActivity;
import com.dragons.aurora.model.App;
import com.dragons.aurora.view.SearchResultAppBadge;

import java.util.List;

public class EndlessAppsAdapter extends InstalledAppsAdapter {

    private List<App> appsToAdd;
    private Context context;
    private InstalledAppsAdapter.ViewHolder viewHolder;

    public EndlessAppsAdapter(Context context, List<App> appsToAdd) {
        super(context, appsToAdd);
        this.context = context;
        this.appsToAdd = appsToAdd;
    }

    @Override
    public void setViewHolder(ViewHolder viewHolder) {
        this.viewHolder = viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull InstalledAppsAdapter.ViewHolder holder, int position) {
        setViewHolder(holder);
        final App app = appsToAdd.get(position);
        final SearchResultAppBadge searchResultAppBadge = new SearchResultAppBadge();

        searchResultAppBadge.setApp(app);
        searchResultAppBadge.setView(holder.view);
        searchResultAppBadge.draw();

        viewHolder.list_container.setOnClickListener(v -> {
            Context context = viewHolder.view.getContext();
            context.startActivity(DetailsActivity.getDetailsIntent(context, app.getPackageName()));
        });

        setup3dotMenu(viewHolder, app, position);
    }
}
