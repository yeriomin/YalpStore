package in.dragons.galaxy;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

public class AllCategoriesAdapter extends RecyclerView.Adapter<AllCategoriesAdapter.ViewHolder> {

    private Context context;
    private Map<String, String> categories;

    private Integer[] categoriesImg = {
            R.drawable.ic_android_wear,
            R.drawable.ic_art_design,
            R.drawable.ic_auto_vehicles,
            R.drawable.ic_beauty,
            R.drawable.ic_books_reference,
            R.drawable.ic_business,
            R.drawable.ic_comics,
            R.drawable.ic_communication,
            R.drawable.ic_dating,
            R.drawable.ic_education,
            R.drawable.ic_entertainment,
            R.drawable.ic_events,
            R.drawable.ic_family,
            R.drawable.ic_finance,
            R.drawable.ic_food_drink,
            R.drawable.ic_games,
            R.drawable.ic_health_fitness,
            R.drawable.ic_house_home,
            R.drawable.ic_libraries_demo,
            R.drawable.ic_lifestyle,
            R.drawable.ic_maps_navigation,
            R.drawable.ic_medical,
            R.drawable.ic_music__audio,
            R.drawable.ic_news_magazines,
            R.drawable.ic_parenting,
            R.drawable.ic_personalization,
            R.drawable.ic_photography,
            R.drawable.ic_productivity,
            R.drawable.ic_shopping,
            R.drawable.ic_social,
            R.drawable.ic_sports,
            R.drawable.ic_tools,
            R.drawable.ic_travel_local,
            R.drawable.ic_video_editors,
            R.drawable.ic_weather,
    };

    private View view;
    private ViewHolder viewHolder;
    private SharedPreferencesTranslator translator;

    public AllCategoriesAdapter(Context context, Map<String, String> categories) {
        this.categories = categories;
        this.context = context;
        translator = new SharedPreferencesTranslator(PreferenceManager.getDefaultSharedPreferences(context));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView topLabel;
        public ImageView topImage;
        public LinearLayout topContainer;

        public ViewHolder(View v) {
            super(v);
            topLabel = (TextView) v.findViewById(R.id.top_cat_name);
            topImage = (ImageView) v.findViewById(R.id.top_cat_img);
            topContainer = (LinearLayout) v.findViewById(R.id.all_cat_container);
        }
    }

    @Override
    public AllCategoriesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.all_cat_item, parent, false);
        viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.topLabel.setText(translator.getString(new ArrayList<>(categories.keySet()).get(position)));
        holder.topImage.setImageDrawable(context.getResources().getDrawable(categoriesImg[position]));
        holder.topContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategoryAppsActivity.start(context, new ArrayList<>(categories.keySet()).get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }
}
