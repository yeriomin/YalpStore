package com.dragons.aurora.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.dragons.aurora.R;
import com.dragons.aurora.activities.DetailsActivity;
import com.dragons.aurora.fragment.DetailsFragment;
import com.dragons.aurora.model.App;
import com.dragons.aurora.view.UpdatableAppBadge;

import java.util.List;

public class UpdatableAppsAdapter extends RecyclerView.Adapter<UpdatableAppsAdapter.ViewHolder> {

    private List<App> appsToAdd;
    private UpdatableAppBadge updatableAppBadge;

    public UpdatableAppsAdapter(List<App> appsToAdd) {
        this.appsToAdd = appsToAdd;
    }

    public void add(int position, App app) {
        appsToAdd.add(position, app);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        appsToAdd.remove(position);
        notifyItemRemoved(position);
    }

    @NonNull
    @Override
    public UpdatableAppsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.updatable_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UpdatableAppsAdapter.ViewHolder holder, int position) {
        final App app = appsToAdd.get(position);
        updatableAppBadge = new UpdatableAppBadge();
        updatableAppBadge.setApp(app);
        updatableAppBadge.setView(holder.view);
        updatableAppBadge.draw();

        holder.list_container.setOnClickListener(v -> {
            Context context = holder.view.getContext();
            DetailsFragment.app = app;
            context.startActivity(DetailsActivity.getDetailsIntent(context, DetailsFragment.app.getPackageName()));
        });
    }

    @Override
    public int getItemCount() {
        return appsToAdd.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private LinearLayout list_container;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            list_container = view.findViewById(R.id.list_container);
        }
    }

}
