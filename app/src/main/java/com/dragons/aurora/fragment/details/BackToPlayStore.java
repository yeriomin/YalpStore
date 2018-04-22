package com.dragons.aurora.fragment.details;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.dragons.aurora.fragment.DetailsFragment;
import com.percolate.caffeine.ViewUtils;

import com.dragons.aurora.activities.DetailsActivity;
import com.dragons.aurora.R;
import com.dragons.aurora.model.App;
import com.dragons.aurora.task.playstore.PurchaseTask;

public class BackToPlayStore extends AbstractHelper {

    static private final String PLAY_STORE_PACKAGE_NAME = "com.android.vending";

    public BackToPlayStore(DetailsFragment fragment, App app) {
        super(fragment, app);
    }

    @Override
    public void draw() {
        if (!isPlayStoreInstalled() || !app.isInPlayStore()) {
            return;
        }
        ViewUtils.findViewById(fragment.getActivity(),R.id.to_play_store_cnt).setVisibility(View.VISIBLE);
        ImageView toPlayStore = (ImageView) fragment.getActivity().findViewById(R.id.to_play_store);
        toPlayStore.setVisibility(View.VISIBLE);
        toPlayStore.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(PurchaseTask.URL_PURCHASE + app.getPackageName()));
            fragment.getActivity().startActivity(i);
        });
    }

    private boolean isPlayStoreInstalled() {
        try {
            return null != fragment.getActivity().getPackageManager().getPackageInfo(PLAY_STORE_PACKAGE_NAME, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}