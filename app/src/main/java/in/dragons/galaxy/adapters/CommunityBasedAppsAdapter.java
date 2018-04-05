package in.dragons.galaxy.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import in.dragons.galaxy.R;
import in.dragons.galaxy.activities.DetailsActivity;

public class CommunityBasedAppsAdapter extends RecyclerView.Adapter<CommunityBasedAppsAdapter.MyViewHolderInst> {

    String pkg;
    private List<FeaturedHolder> FeaturedAppsH;
    private Context context;

    class MyViewHolderInst extends RecyclerView.ViewHolder {
        TextView cbased_name, cbased_price;
        ImageView cbased_image;
        RelativeLayout cbased_layout;

        MyViewHolderInst(View view) {
            super(view);
            cbased_name = view.findViewById(R.id.cbased_name);
            cbased_image = view.findViewById(R.id.cbased_image);
            cbased_price = view.findViewById(R.id.cbased_price);
            cbased_layout = view.findViewById(R.id.cbased_layout);
        }
    }


    public CommunityBasedAppsAdapter(List<FeaturedHolder> FeaturedAppsH, Context context) {
        this.FeaturedAppsH = FeaturedAppsH;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolderInst onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comunity_based_adapter, parent, false);
        return new MyViewHolderInst(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolderInst holder, int position) {
        final FeaturedHolder featuredHolder = FeaturedAppsH.get(position);
        holder.cbased_name.setText(featuredHolder.cbased_name);
        holder.cbased_layout.setOnClickListener(v -> {
            context.startActivity(DetailsActivity.getDetailsIntent(context, featuredHolder.cbased_packagename));
        });
        Picasso.with(context)
                .load(featuredHolder.cbased_appicon)
                .placeholder(R.drawable.ic_placeholder)
                .into(holder.cbased_image);
    }

    @Override
    public int getItemCount() {
        return FeaturedAppsH.size();
    }

    public static class FeaturedHolder {
        String cbased_name;
        String cbased_packagename;
        String cbased_appicon;

        public FeaturedHolder(String name, String pkg, String app_icon) {
            this.cbased_name = name;
            this.cbased_packagename = pkg;
            this.cbased_appicon = app_icon;
        }

    }
}

