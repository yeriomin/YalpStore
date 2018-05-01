package com.dragons.aurora.adapters;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dragons.aurora.R;
import com.dragons.aurora.SharedPreferencesTranslator;
import com.dragons.aurora.activities.CategoryAppsActivity;
import com.percolate.caffeine.ViewUtils;

public class TopCategoriesAdapter extends RecyclerView.Adapter<TopCategoriesAdapter.ViewHolder> {

    private Context context;
    private String[] categories;

    private SharedPreferencesTranslator translator;

    public TopCategoriesAdapter(Context context, String[] topCategories) {
        this.categories = topCategories;
        this.context = context;
        this.translator = new SharedPreferencesTranslator(PreferenceManager.getDefaultSharedPreferences(context));
    }

    @NonNull
    @Override
    public TopCategoriesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.top_cat_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.topLabel.setText(translator.getString(categories[position]));
        holder.topLabel.setOnClickListener(v -> CategoryAppsActivity.start(context, categories[holder.getAdapterPosition()]));
    }

    @Override
    public int getItemCount() {
        return categories.length;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView topLabel;

        ViewHolder(View v) {
            super(v);
            topLabel = ViewUtils.findViewById(v, R.id.top_cat_name);
        }
    }
}
