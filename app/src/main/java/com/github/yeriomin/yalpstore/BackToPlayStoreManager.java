package com.github.yeriomin.yalpstore;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.model.App;

public class BackToPlayStoreManager extends DetailsManager {

    static private final String PLAY_STORE_PACKAGE_NAME = "com.android.vending";

    public BackToPlayStoreManager(DetailsActivity activity, App app) {
        super(activity, app);
    }

    @Override
    public void draw() {
        if (!isPlayStoreInstalled()) {
            return;
        }
        TextView toPlayStore = (TextView) activity.findViewById(R.id.to_play_store);
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
        for (PackageInfo packageInfo: activity.getPackageManager().getInstalledPackages(PackageManager.GET_META_DATA)) {
            if (packageInfo.packageName.equals(PLAY_STORE_PACKAGE_NAME)) {
                return true;
            }
        }
        return false;
    }
}
