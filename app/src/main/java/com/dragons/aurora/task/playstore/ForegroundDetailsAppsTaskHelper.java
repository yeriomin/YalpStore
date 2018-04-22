package com.dragons.aurora.task.playstore;

import android.content.Context;
import android.content.pm.PackageManager;

import com.dragons.aurora.ContextUtil;
import com.dragons.aurora.R;
import com.dragons.aurora.model.App;
import com.dragons.aurora.model.AppBuilder;
import com.dragons.aurora.model.ReviewBuilder;
import com.dragons.aurora.playstoreapiv2.DetailsResponse;
import com.dragons.aurora.playstoreapiv2.GooglePlayAPI;
import com.dragons.aurora.playstoreapiv2.GooglePlayException;

import java.io.IOException;

public abstract class ForegroundDetailsAppsTaskHelper extends ForegroundUpdatableAppsTaskHelper {

    protected Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    protected App getResult(GooglePlayAPI api, String packageName) throws IOException {
        DetailsResponse response = api.details(packageName);
        App app = AppBuilder.build(response.getDocV2());
        if (response.hasUserReview()) {
            app.setUserReview(ReviewBuilder.build(response.getUserReview()));
        }
        PackageManager pm = this.getActivity().getPackageManager();
        try {
            app.getPackageInfo().applicationInfo = pm.getApplicationInfo(packageName, 0);
            app.getPackageInfo().versionCode = pm.getPackageInfo(packageName, 0).versionCode;
            app.setInstalled(true);
        } catch (PackageManager.NameNotFoundException e) {
            // App is not installed
        }
        return app;
    }

    @Override
    protected void processIOException(IOException e) {
        if (null != e && e instanceof GooglePlayException && ((GooglePlayException) e).getCode() == 404) {
            ContextUtil.toast(this.getActivity(), R.string.details_not_available_on_play_store);
        }
    }
}