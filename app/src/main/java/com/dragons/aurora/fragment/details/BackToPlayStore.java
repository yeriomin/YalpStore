package com.dragons.aurora.fragment.details;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.percolate.caffeine.ViewUtils;

import com.dragons.aurora.activities.DetailsActivity;
import com.dragons.aurora.R;
import com.dragons.aurora.model.App;
import com.dragons.aurora.task.playstore.PurchaseTask;

public class BackToPlayStore extends Abstract {

    static private final String PLAY_STORE_PACKAGE_NAME = "com.android.vending";

    public BackToPlayStore(DetailsActivity activity, App app) {
        super(activity, app);
    }

    @Override
    public void draw() {
        if (!isPlayStoreInstalled() || !app.isInPlayStore()) {
            return;
        }
        ViewUtils.findViewById(activity,R.id.to_play_store_cnt).setVisibility(View.VISIBLE);
        ImageView toPlayStore = (ImageView) activity.findViewById(R.id.to_play_store);
        toPlayStore.setVisibility(View.VISIBLE);
        toPlayStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(PurchaseTask.URL_PURCHASE + app.getPackageName()));
                activity.startActivity(i);
            }
        });
    }

    private boolean isPlayStoreInstalled() {
        try {
            return null != activity.getPackageManager().getPackageInfo(PLAY_STORE_PACKAGE_NAME, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}