package in.dragons.galaxy;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

    private View view;
    private ViewHolder viewHolder;
    private SharedPreferencesTranslator translator;

    public TopCategoriesAdapter(Context context, String[] topCategories) {
        this.categories = topCategories;
        this.context = context;
        translator = new SharedPreferencesTranslator(PreferenceManager.getDefaultSharedPreferences(context));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView topLabel;
        public ImageView topImage;

        public ViewHolder(View v) {
            super(v);
            topLabel = (TextView) v.findViewById(R.id.top_cat_name);
            topImage = (ImageView) v.findViewById(R.id.top_cat_img);
        }
    }

    @Override
    public TopCategoriesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.top_cat_item, parent, false);
        viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.topLabel.setText(translator.getString(categories[position]));
        holder.topImage.setImageDrawable(context.getResources().getDrawable(categoriesImg[position]));
        holder.topImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategoryAppsActivity.start(context, categories[position]);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.length;
    }
}
