package com.dragons.aurora.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.Format;
import java.util.List;
import java.util.Locale;

import com.dragons.aurora.R;
import com.dragons.aurora.activities.DetailsActivity;

public class FeaturedAdapter extends RecyclerView.Adapter<FeaturedAdapter.MyViewHolderInst> {

    private List<FeaturedHolder> FeaturedAppsH;
    private Context context;

    class MyViewHolderInst extends RecyclerView.ViewHolder {
        TextView title, developer, ratingText;
        ImageView icon, menu3dot;
        LinearLayout card;
        RatingBar ratingBar;

        MyViewHolderInst(View view) {
            super(view);
            title = view.findViewById(R.id.featured_name);
            developer = view.findViewById(R.id.app_by);
            icon = view.findViewById(R.id.featured_image);
            card = view.findViewById(R.id.background);
            ratingBar = view.findViewById(R.id.ratingBar);
            ratingText = view.findViewById(R.id.ratingText);
            menu3dot = view.findViewById(R.id.menu_3dot);
        }
    }

    public FeaturedAdapter(List<FeaturedHolder> FeaturedAppsH, Context context) {
        this.FeaturedAppsH = FeaturedAppsH;
        this.context = context;
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
        holder.title.setText(featuredHolder.title);
        holder.developer.setText(featuredHolder.developer);
        holder.ratingBar.setRating((float) featuredHolder.rating);
        holder.ratingText.setText(String.format(Locale.getDefault(),"%.1f",featuredHolder.rating));

        holder.card.setOnClickListener(v -> context
                .startActivity(DetailsActivity.getDetailsIntent(context, featuredHolder.id)));

        Picasso.with(context)
                .load(featuredHolder.icon)
                .placeholder(R.color.transparent)
                .into(holder.icon, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap bitmap = ((BitmapDrawable) holder.icon.getDrawable()).getBitmap();
                        if (bitmap != null)
                            getPalette(bitmap, holder.card, holder.developer);
                    }

                    @Override
                    public void onError() {
                        holder.developer.setTextColor(Color.WHITE);
                        holder.card.setBackgroundColor(Color.BLACK);
                    }
                });

        holder.menu3dot.setOnClickListener(this::showPopup);
    }

    private void getPalette(Bitmap bitmap, LinearLayout card, TextView developer) {
        Palette.from(bitmap)
                .generate(palette -> {
                    Palette.Swatch mySwatch = palette.getDarkVibrantSwatch();
                    if (mySwatch != null) {
                        developer.setTextColor(mySwatch.getTitleTextColor());
                        card.setBackgroundColor(mySwatch.getRgb());
                    }
                });
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
        return FeaturedAppsH.size();
    }

    public static class FeaturedHolder {
        String title;
        String id;
        String developer;
        String icon;
        double rating;
        String price;

        public FeaturedHolder(String title, String id, String developer, String icon,
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