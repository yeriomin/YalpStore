package in.dragons.galaxy.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import in.dragons.galaxy.R;
import in.dragons.galaxy.activities.DetailsActivity;

public class ComunityBasedAppsAdapter extends RecyclerView.Adapter<ComunityBasedAppsAdapter.MyViewHolderInst> {

    String pkg;
    private List<FeaturedHolder> FeaturedAppsH;

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
            cbased_layout.setOnClickListener(v -> {
                view.getContext().startActivity(DetailsActivity.getDetailsIntent(view.getContext(), pkg));
            });
        }
    }


    public ComunityBasedAppsAdapter(List<FeaturedHolder> FeaturedAppsH) {
        this.FeaturedAppsH = FeaturedAppsH;
    }

    @Override
    public MyViewHolderInst onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comunity_based_adapter, parent, false);

        return new MyViewHolderInst(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolderInst holder, int position) {
        final FeaturedHolder featuredHolder = FeaturedAppsH.get(position);
        holder.cbased_name.setText(featuredHolder.cbased_name);
        pkg = featuredHolder.cbased_packagename;
    }

    @Override
    public int getItemCount() {
        return FeaturedAppsH.size();
    }

    public static class FeaturedHolder {
        String cbased_name;
        String cbased_packagename;

        public FeaturedHolder(String name, String pkg) {
            this.cbased_name = name;
            this.cbased_packagename = pkg;
        }

    }
}

