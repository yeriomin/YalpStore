package com.github.yeriomin.yalpstore.task.playstore;

import android.content.pm.PackageManager;

import com.github.yeriomin.playstoreapi.DetailsResponse;
import com.github.yeriomin.playstoreapi.GooglePlayAPI;
import com.github.yeriomin.playstoreapi.GooglePlayException;
import com.github.yeriomin.yalpstore.ContextUtil;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.model.AppBuilder;
import com.github.yeriomin.yalpstore.model.ReviewBuilder;

import java.io.IOException;

public class DetailsTask extends PlayStorePayloadTask<App> {

    protected String packageName;

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    @Override
    protected void processIOException(IOException e) {
        if (null != e && e instanceof GooglePlayException && ((GooglePlayException) e).getCode() == 404) {
            ContextUtil.toast(this.context, R.string.details_not_available_on_play_store);
        }
    }

    @Override
    protected App getResult(GooglePlayAPI api, String... arguments) throws IOException {
        DetailsResponse response = api.details(packageName);
        App app = AppBuilder.build(response.getDocV2());
        if (response.hasUserReview()) {
            app.setUserReview(ReviewBuilder.build(response.getUserReview()));
        }
        PackageManager pm = context.getPackageManager();
        try {
            app.getPackageInfo().applicationInfo = pm.getApplicationInfo(packageName, 0);
            app.getPackageInfo().versionCode = pm.getPackageInfo(packageName, 0).versionCode;
            app.setInstalled(true);
        } catch (PackageManager.NameNotFoundException e) {
            // App is not installed
        }
        return app;
    }
}
