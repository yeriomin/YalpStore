package in.dragons.galaxy.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import in.dragons.galaxy.R;
import in.dragons.galaxy.activities.DetailsActivity;

public class FeaturedAdapter extends RecyclerView.Adapter<FeaturedAdapter.MyViewHolderInst> {

    private List<FeaturedHolder> FeaturedAppsH;
    private Context context;
    private int defaultStartColor = 0x3949AB;
    private int defaultEndColor = 0x5C6BC0;

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
            background = view.findViewById(R.id.background);
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
        holder.featured_name.setText(featuredHolder.featured_name);
        holder.app_by.setText(featuredHolder.app_by);
        holder.featured_install.setOnClickListener(v -> context
                .startActivity(DetailsActivity.getDetailsIntent(context, featuredHolder.featured_packagename)));
        Picasso.with(context)
                .load(featuredHolder.app_icon)
                .placeholder(R.drawable.ic_placeholder)
                .into(holder.featured_image, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap bitmap = ((BitmapDrawable) holder.featured_image.getDrawable()).getBitmap();
                        if (bitmap != null)
                            getPalette(bitmap, holder.background);
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    private void getPalette(Bitmap bitmap, RelativeLayout background) {
        if (bitmap != null)
            Palette.from(bitmap)
                    .generate(palette ->
                            drawGradient(palette.getDarkMutedColor(defaultStartColor),
                                    palette.getDarkVibrantColor(defaultEndColor),
                                    background));
    }

    private void drawGradient(int startColor, int endColor, RelativeLayout r) {
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[]{startColor, endColor});
        gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        gradientDrawable.setGradientRadius(0.0f);
        r.setBackgroundDrawable(gradientDrawable);
    }

    @Override
    public int getItemCount() {
        return FeaturedAppsH.size();
    }

    public static class FeaturedHolder {
        String featured_name;
        String featured_packagename;
        String app_by;
        String app_icon;

        public FeaturedHolder(String name, String pkg, String app_by, String app_icon) {
            this.featured_name = name;
            this.featured_packagename = pkg;
            this.app_by = app_by;
            this.app_icon = app_icon;
        }

    }
}

