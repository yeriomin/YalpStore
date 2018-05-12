package com.dragons.aurora.fragment.details;

import android.app.SearchManager;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.dragons.aurora.R;
import com.dragons.aurora.activities.SearchActivity;
import com.dragons.aurora.fragment.DetailsFragment;
import com.dragons.aurora.model.App;
import com.dragons.aurora.task.ClusterTaskHelper;
import com.dragons.aurora.view.ClusterAppsCard;
import com.percolate.caffeine.ViewUtils;

public class AppLists extends AbstractHelper {

    public AppLists(DetailsFragment fragment, App app) {
        super(fragment, app);
    }

    @Override
    public void draw() {
        LinearLayout relatedLinksLayout = fragment.getActivity().findViewById(R.id.cluster_links);
        for (String label : app.getRelatedLinks().keySet()) {
            relatedLinksLayout.setVisibility(View.VISIBLE);
            relatedLinksLayout.addView(buildClusterAppsCard(app.getRelatedLinks().get(label), label));
        }
        addAppsByThisDeveloper();
    }

    private ClusterAppsCard buildClusterAppsCard(String URL, String label) {
        ClusterAppsCard clusterAppsCard = new ClusterAppsCard(fragment.getActivity(), label);
        RecyclerView recyclerView = clusterAppsCard.findViewById(R.id.m_apps_recycler);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        clusterAppsCard.setLayoutParams(params);
        clusterAppsCard.setGravity(Gravity.CENTER_VERTICAL);

        new ClusterTaskHelper(fragment.getContext(), recyclerView).getClusterApps(URL);
        return clusterAppsCard;
    }

    private void addAppsByThisDeveloper() {
        ViewUtils.findViewById(fragment.getActivity(), R.id.apps_by_same_developer_cnt).setVisibility(View.VISIBLE);
        ImageView imageView = fragment.getActivity().findViewById(R.id.apps_by_same_developer);
        imageView.setVisibility(View.VISIBLE);
        imageView.setOnClickListener(v -> {
            Intent intent = new Intent(fragment.getActivity(), SearchActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_SEARCH);
            intent.putExtra(SearchManager.QUERY, SearchActivity.PUB_PREFIX + app.getDeveloperName());
            fragment.getActivity().startActivity(intent);
        });
    }
}
