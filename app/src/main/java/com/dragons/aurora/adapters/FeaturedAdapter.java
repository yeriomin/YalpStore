package com.dragons.aurora.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dragons.aurora.R;
import com.dragons.aurora.activities.DetailsActivity;
import com.dragons.aurora.model.App;
import com.dragons.aurora.task.playstore.DetailsTask;
import com.dragons.aurora.task.playstore.PurchaseTask;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

public class FeaturedAdapter extends RecyclerView.Adapter<FeaturedAdapter.MyViewHolderInst> {

    private List<FeaturedHolder> FeaturedAppsH;
    private Context context;

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
        holder.ratingText.setText(String.format(Locale.getDefault(), "%.1f", featuredHolder.rating));

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

        holder.download_app.setOnClickListener(click -> {
            DetailsAndPurchaseTask task = new DetailsAndPurchaseTask();
            task.setPackageName(featuredHolder.id);
            task.setContext(context);
            task.execute();
        });
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

    static class DetailsAndPurchaseTask extends DetailsTask {

        @Override
        protected void onPostExecute(App app) {
            if (success()) {
                getPurchaseTask(app).execute();
            } else {
                Toast.makeText(context,
                        R.string.details_not_available_on_play_store,
                        Toast.LENGTH_SHORT).show();
            }
        }

        private PurchaseTask getPurchaseTask(App app) {
            PurchaseTask task = new PurchaseTask();
            task.setApp(app);
            task.setContext(context);
            return task;
        }
    }

    class MyViewHolderInst extends RecyclerView.ViewHolder {
        TextView title, developer, ratingText;
        ImageView icon, download_app;
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
            download_app = view.findViewById(R.id.download_app);
        }
    }
}