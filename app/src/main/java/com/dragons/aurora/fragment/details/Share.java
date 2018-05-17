package com.dragons.aurora.fragment.details;

import android.content.Intent;
import android.widget.ImageView;

import com.dragons.aurora.R;
import com.dragons.aurora.fragment.DetailsFragment;
import com.dragons.aurora.model.App;

public class Share extends AbstractHelper {

    static private String PLAYSTORE_LINK_PREFIX = "https://play.google.com/store/apps/details?id=";

    public Share(DetailsFragment fragment, App app) {
        super(fragment, app);
    }

    @Override
    public void draw() {
        ImageView share = (ImageView) fragment.getActivity().findViewById(R.id.share);
        share.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, app.getDisplayName());
            i.putExtra(Intent.EXTRA_TEXT, PLAYSTORE_LINK_PREFIX + app.getPackageName());
            fragment.getActivity().startActivity(Intent.createChooser(i, fragment.getActivity().getString(R.string.details_share)));
        });
    }
}