package in.dragons.galaxy.adapters;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.percolate.caffeine.ViewUtils;

import in.dragons.galaxy.R;
import in.dragons.galaxy.SharedPreferencesTranslator;
import in.dragons.galaxy.activities.CategoryAppsActivity;

public class TopCategoriesAdapter extends RecyclerView.Adapter<TopCategoriesAdapter.ViewHolder> {

    private Context context;
    private String[] categories;

    private Integer[] categoriesImg = {
            R.drawable.ic_photography,
            R.drawable.ic_music__audio,
            R.drawable.ic_entertainment,
            R.drawable.ic_shopping,
            R.drawable.ic_personalization,
            R.drawable.ic_social,
            R.drawable.ic_communication
    };

    private SharedPreferencesTranslator translator;

    public TopCategoriesAdapter(Context context, String[] topCategories) {
        this.categories = topCategories;
        this.context = context;
        this.translator = new SharedPreferencesTranslator(PreferenceManager.getDefaultSharedPreferences(context));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView topLabel;
        ImageView topImage;

        ViewHolder(View v) {
            super(v);
            topLabel = ViewUtils.findViewById(v, R.id.top_cat_name);
            topImage = ViewUtils.findViewById(v, R.id.top_cat_img);
        }
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
        holder.topImage.setImageDrawable(context.getResources().getDrawable(categoriesImg[holder.getAdapterPosition()]));
        holder.topLabel.setOnClickListener(v -> CategoryAppsActivity.start(context, categories[holder.getAdapterPosition()]));
    }

    @Override
    public int getItemCount() {
        return categories.length;
    }
}
