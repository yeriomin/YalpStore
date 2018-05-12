package com.dragons.aurora.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.dragons.aurora.CircleTransform;
import com.dragons.aurora.R;
import com.dragons.aurora.activities.AccountsActivity;
import com.dragons.aurora.activities.CategoryAppsActivity;
import com.dragons.aurora.playstoreapiv2.GooglePlayAPI;
import com.dragons.aurora.task.CategoryTaskHelper;
import com.dragons.aurora.task.FeaturedTaskHelper;
import com.dragons.aurora.view.AdaptiveToolbar;
import com.dragons.aurora.view.MoreAppsCard;
import com.dragons.aurora.view.TagView;
import com.squareup.picasso.Picasso;

public class HomeFragment extends UtilFragment {

    private AdaptiveToolbar adtb;
    private View view;
    private LinearLayout topLinks;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            if ((ViewGroup) view.getParent() != null)
                ((ViewGroup) view.getParent()).removeView(view);
            return view;
        }
        view = inflater.inflate(R.layout.fragment_home, container, false);

        adtb = view.findViewById(R.id.adtb);
        adtb.getAvatar_icon().setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AccountsActivity.class);
            intent.putExtra("account_profile_animate", true);
            startActivity(intent);
        });

        initTags();

        topLinks = view.findViewById(R.id.top_links);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isLoggedIn() && isConnected(getContext())) {
            setUser();
            if (topLinks.getVisibility() == View.GONE) {
                setupTopFeatured();
                drawCategories();
            }
        } else {
            resetUser();
            LoginFirst();
        }
    }

    protected void setUser() {
        if (isGoogle()) {
            Picasso.with(getActivity())
                    .load(PreferenceFragment.getString(getActivity(), "GOOGLE_URL"))
                    .placeholder(R.drawable.ic_user_placeholder)
                    .transform(new CircleTransform())
                    .into(adtb.getAvatar_icon());
        } else {
            (adtb.getAvatar_icon()).setImageDrawable(getResources()
                    .getDrawable(R.drawable.ic_dummy_avatar));
        }
    }

    protected void resetUser() {
        (adtb.getAvatar_icon()).setImageDrawable(getResources()
                .getDrawable(R.drawable.ic_user_placeholder));
    }

    protected void initTags() {
        setupTag(view, R.id.tag_gamesAction, "GAME_ACTION");
        setupTag(view, R.id.tag_family, "FAMILY");
        setupTag(view, R.id.tag_gamesRacing, "GAME_RACING");
        setupTag(view, R.id.tag_travel, "TRAVEL_AND_LOCAL");
        setupTag(view, R.id.tag_social, "SOCIAL");
    }

    protected void setupTag(View v, int viewID, String Category) {
        TagView tagView = v.findViewById(viewID);
        tagView.setOnClickListener(click -> CategoryAppsActivity.start(v.getContext(), Category));
    }

    protected void setupTopFeatured() {
        RecyclerView topGrossingGames = view.findViewById(R.id.top_featured_games);
        RecyclerView topGrossingApps = view.findViewById(R.id.top_featured_apps);

        new FeaturedTaskHelper(getContext(), topGrossingGames).getCategoryApps("GAME", GooglePlayAPI.SUBCATEGORY.TOP_GROSSING);
        new FeaturedTaskHelper(getContext(), topGrossingApps).getCategoryApps("", GooglePlayAPI.SUBCATEGORY.TOP_FREE);
    }

    protected void drawCategories() {
        GooglePlayAPI.SUBCATEGORY subcategory = GooglePlayAPI.SUBCATEGORY.TOP_GROSSING;
        LinearLayout topLinksLayout = view.findViewById(R.id.top_links);
        topLinksLayout.setVisibility(View.VISIBLE);
        topLinksLayout.addView(buildAppsCard("TOOLS", subcategory, "Tools & Utilities"));
        topLinksLayout.addView(buildAppsCard("COMMUNICATION", subcategory, "Communication"));
        topLinksLayout.addView(buildAppsCard("MUSIC_AND_AUDIO", subcategory, "Music & Audio"));
        topLinksLayout.addView(buildAppsCard("PERSONALIZATION", subcategory, "Personalization"));
    }

    private MoreAppsCard buildAppsCard(String categoryId, GooglePlayAPI.SUBCATEGORY subcategory, String label) {
        MoreAppsCard moreAppsCard = new MoreAppsCard(getContext(), categoryId, label);

        RecyclerView recyclerView = moreAppsCard.findViewById(R.id.m_apps_recycler);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        moreAppsCard.setLayoutParams(params);
        moreAppsCard.setGravity(Gravity.CENTER_VERTICAL);

        new CategoryTaskHelper(getActivity(), recyclerView).getCategoryApps(categoryId, subcategory);
        return moreAppsCard;
    }
}
