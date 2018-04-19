package com.dragons.aurora.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import com.dragons.aurora.R;
import com.dragons.aurora.activities.FullscreenImageActivity;

public class SmallScreenshotsAdapter extends RecyclerView.Adapter<SmallScreenshotsAdapter.MyViewHolderInst> {
    private List<SmallScreenshotsAdapter.Holder> ssholder;
    private Context context;


    class MyViewHolderInst extends RecyclerView.ViewHolder {
        ImageView ss_image;

        MyViewHolderInst(View view) {
            super(view);
            ss_image
                    = view.findViewById(R.id.scrn_itm_s);
        }
    }

    public SmallScreenshotsAdapter(List<SmallScreenshotsAdapter.Holder> FeaturedAppsH, Context context) {
        this.ssholder = FeaturedAppsH;
        this.context = context;
    }

    @NonNull
    @Override
    public SmallScreenshotsAdapter.MyViewHolderInst onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.screenshots_item_small, parent, false);
        return new SmallScreenshotsAdapter.MyViewHolderInst(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final SmallScreenshotsAdapter.MyViewHolderInst holder, int position) {
        final SmallScreenshotsAdapter.Holder ssholder = this.ssholder.get(position);
        String url = ssholder.url.get(position);
        Picasso.with(context)
                .load(url)
                .placeholder(R.drawable.screenshot_bg)
                .into(holder.ss_image);
        holder.ss_image.setOnClickListener(v -> {
            Intent intent = new Intent(context, FullscreenImageActivity.class);
            intent.putExtra(FullscreenImageActivity.INTENT_SCREENSHOT_NUMBER, position);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return ssholder.size();
    }

    public static class Holder {
        List<String> url;

        public Holder(List<String> url) {
            this.url = url;
        }
    }
}

