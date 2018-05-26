package com.dragons.aurora.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.dragons.aurora.R;
import com.dragons.aurora.model.App;
import com.dragons.aurora.view.UpdatableAppBadge;
import com.github.florent37.shapeofview.shapes.RoundRectView;

import java.util.List;

public class UpdatableAppsAdapter extends RecyclerView.Adapter<UpdatableAppsAdapter.ViewHolder> {

    private List<App> appsToAdd;
    private Context context;

    public UpdatableAppsAdapter(Context context, List<App> appsToAdd) {
        this.context = context;
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

    public void remove(String packageName) {
        int i = 0;
        for (App app : appsToAdd) {
            if (app.getPackageName().equals(packageName)) {
                remove(i);
                break;
            }
            i++;
        }
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
        final UpdatableAppBadge updatableAppBadge = new UpdatableAppBadge();

        updatableAppBadge.setApp(app);
        updatableAppBadge.setView(holder.view);
        updatableAppBadge.draw();
        updatableAppBadge.drawButtons();

        holder.app = app;
    }

    @Override
    public int getItemCount() {
        return appsToAdd.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public RoundRectView viewForeground;
        public App app;
        private View view;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            viewForeground = view.findViewById(R.id.view_foreground);
        }
    }
}
