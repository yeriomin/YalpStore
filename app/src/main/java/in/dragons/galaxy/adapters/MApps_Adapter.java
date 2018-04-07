package in.dragons.galaxy.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import in.dragons.galaxy.R;
import in.dragons.galaxy.activities.DetailsActivity;

public class MApps_Adapter extends RecyclerView.Adapter<MApps_Adapter.MyViewHolderInst> {
    private List<MAppsHolder> mappsholder;
    private Context context;

    class MyViewHolderInst extends RecyclerView.ViewHolder {
        TextView mapps_name, ratingText, price;
        ImageView mapps_image, menu3dot;
        RelativeLayout mapps_layout;
        RatingBar ratingBar;

        MyViewHolderInst(View view) {
            super(view);
            mapps_name = view.findViewById(R.id.m_apps_name);
            mapps_image = view.findViewById(R.id.m_apps_img);
            mapps_layout = view.findViewById(R.id.m_apps_layout);
            ratingBar = view.findViewById(R.id.ratingBar);
            ratingText = view.findViewById(R.id.ratingText);
            price = view.findViewById(R.id.app_price);
            menu3dot = view.findViewById(R.id.menu_3dot);
        }
    }

    public MApps_Adapter(List<MAppsHolder> FeaturedAppsH, Context context) {
        this.mappsholder = FeaturedAppsH;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolderInst onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.more_apps_item, parent, false);
        return new MyViewHolderInst(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolderInst holder, int position) {
        final MAppsHolder mappsholder = this.mappsholder.get(position);
        holder.mapps_name.setText(mappsholder.title);
        holder.ratingBar.setRating((float) mappsholder.rating);
        holder.ratingText.setText(String.format("%.1f", mappsholder.rating));
        holder.price.setText(mappsholder.price.isEmpty() ? R.string.category_appFree : R.string.category_appPaid);
        holder.mapps_layout.setOnClickListener(v ->
                context.startActivity(DetailsActivity.getDetailsIntent(context, mappsholder.id)));
        Picasso.with(context)
                .load(mappsholder.icon)
                .placeholder(android.R.color.transparent)
                .into(holder.mapps_image);

        holder.menu3dot.setOnClickListener(this::showPopup);
    }

    private void showPopup(View v) {
        PopupMenu popup = new PopupMenu(context, v, Gravity.CENTER);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.list_actions, popup.getMenu());
        popup.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.app_install:
                case R.id.app_wishlist:
            }
            return false;
        });
        popup.show();
    }

    @Override
    public int getItemCount() {
        return mappsholder.size();
    }

    public static class MAppsHolder {
        String title;
        String id;
        String developer;
        String icon;
        double rating;
        String price;

        public MAppsHolder(String title, String id, String developer, String icon,
                           double rating, String price) {
            this.title = title;
            this.id = id;
            this.developer = developer;
            this.icon = icon;
            this.rating = rating;
            this.price = price;
        }
    }
}

