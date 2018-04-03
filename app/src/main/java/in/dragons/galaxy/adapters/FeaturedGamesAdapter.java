package in.dragons.galaxy.adapters;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import in.dragons.galaxy.R;
import in.dragons.galaxy.activities.DetailsActivity;

public class FeaturedGamesAdapter extends RecyclerView.Adapter<FeaturedGamesAdapter.MyViewHolderInst> {

    private String pkg;
    private List<FeaturedHolder> FeaturedAppsH;

    class MyViewHolderInst extends RecyclerView.ViewHolder {
        TextView featured_name, app_by;
        ImageView featured_image;
        RelativeLayout background;
        Button featured_install;

        MyViewHolderInst(View view) {
            super(view);
            featured_name = view.findViewById(R.id.featured_name);
            app_by = view.findViewById(R.id.app_by);
            featured_image = view.findViewById(R.id.featured_image);
            featured_install = view.findViewById(R.id.featured_install);
            featured_install.setOnClickListener(v -> view.getContext().startActivity(DetailsActivity.getDetailsIntent(view.getContext(), pkg)));
            background = view.findViewById(R.id.background);
            background.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.featured_games_default_gradient));
        }
    }


    public FeaturedGamesAdapter(List<FeaturedHolder> FeaturedAppsH) {
        this.FeaturedAppsH = FeaturedAppsH;
    }

    @NonNull
    @Override
    public MyViewHolderInst onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.features_apps_adapter, parent, false);
        return new MyViewHolderInst(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolderInst holder, int position) {
        final FeaturedHolder featuredHolder = FeaturedAppsH.get(position);
        holder.featured_name.setText(featuredHolder.featured_name);
        holder.app_by.setText(featuredHolder.app_by);
        pkg = featuredHolder.featured_packagename;
    }

    @Override
    public int getItemCount() {
        return FeaturedAppsH.size();
    }

    public static class FeaturedHolder {
        String featured_name;
        String featured_packagename;
        String app_by;

        public FeaturedHolder(String name, String pkg, String app_by) {
            this.featured_name = name;
            this.featured_packagename = pkg;
            this.app_by = app_by;
        }

    }
}

