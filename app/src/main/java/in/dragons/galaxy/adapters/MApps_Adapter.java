package in.dragons.galaxy.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
        TextView mapps_name;
        ImageView mapps_image;
        RelativeLayout mapps_layout;

        MyViewHolderInst(View view) {
            super(view);
            mapps_name = view.findViewById(R.id.m_apps_name);
            mapps_image = view.findViewById(R.id.m_apps_img);
            mapps_layout = view.findViewById(R.id.m_apps_layout);
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
    public void onBindViewHolder(final MyViewHolderInst holder, int position) {
        final MAppsHolder mappsholder = this.mappsholder.get(position);
        holder.mapps_name.setText(mappsholder.mapps_name);
        holder.mapps_layout.setOnClickListener(v -> {
            context.startActivity(DetailsActivity.getDetailsIntent(context, mappsholder.mapps_packagename));
        });
        Picasso.with(context)
                .load(mappsholder.mapps_appicon)
                .placeholder(android.R.color.transparent)
                .into(holder.mapps_image);
    }

    @Override
    public int getItemCount() {
        return mappsholder.size();
    }

    public static class MAppsHolder {
        String mapps_name;
        String mapps_packagename;
        String mapps_appicon;

        public MAppsHolder(String name, String pkg, String app_icon) {
            this.mapps_name = name;
            this.mapps_packagename = pkg;
            this.mapps_appicon = app_icon;
        }

    }
}

