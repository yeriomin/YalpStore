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

public class BigScreenshotsAdapter extends RecyclerView.Adapter<BigScreenshotsAdapter.MyViewHolderInst> {
    private List<BigScreenshotsAdapter.Holder> ssholder;
    private Context context;
    BigScreenshotsAdapter.Holder bsholder;


    class MyViewHolderInst extends RecyclerView.ViewHolder {
        ImageView ss_image;

        MyViewHolderInst(View view) {
            super(view);
            ss_image = view.findViewById(R.id.scrn_itm_b);
        }
    }

    public BigScreenshotsAdapter(List<BigScreenshotsAdapter.Holder> FeaturedAppsH, Context context) {
        this.ssholder = FeaturedAppsH;
        this.context = context;
    }

    @NonNull
    @Override
    public BigScreenshotsAdapter.MyViewHolderInst onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.screenshots_item_big, parent, false);
        return new BigScreenshotsAdapter.MyViewHolderInst(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final BigScreenshotsAdapter.MyViewHolderInst holder, int position) {
        bsholder = this.ssholder.get(position);
        String url = bsholder.url.get(position);
        Picasso.with(context)
                .load(url)
                .placeholder(android.R.color.transparent)
                .into(holder.ss_image);
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

