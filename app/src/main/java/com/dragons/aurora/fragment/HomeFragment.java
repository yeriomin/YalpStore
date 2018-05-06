package com.dragons.aurora.fragment;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.dragons.aurora.CircleTransform;
import com.dragons.aurora.R;
import com.dragons.aurora.activities.AccountsActivity;
import com.dragons.aurora.activities.CategoryAppsActivity;
import com.dragons.aurora.activities.SearchActivity;
import com.dragons.aurora.view.AdaptiveToolbar;
import com.dragons.aurora.view.MoreAppsCard;
import com.dragons.aurora.view.TagView;
import com.squareup.picasso.Picasso;

public class HomeFragment extends UtilFragment {

    AdaptiveToolbar adtb;
    private View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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

        MoreAppsCard fdroidApps = view.findViewById(R.id.fdroid);
        Button moreApps = fdroidApps.findViewById(R.id.m_apps_more);
        moreApps.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_SEARCH);
            intent.putExtra(SearchManager.QUERY, "FDROID");
            getActivity().startActivity(intent);
        });

        MoreAppsCard community = view.findViewById(R.id.community_apps);
        Button moreApps1 =community.findViewById(R.id.m_apps_more);
        moreApps1.setText("");
        moreApps1.setEnabled(false);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isLoggedIn())
            setUser();
        else {
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
}
