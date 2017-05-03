package com.github.yeriomin.yalpstore;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.github.yeriomin.playstoreapi.GooglePlayException;
import com.github.yeriomin.yalpstore.model.App;

import java.io.IOException;

public class DetailsTask extends GoogleApiAsyncTask {

    protected App app;
    protected String packageName;

    public DetailsTask setPackageName(String packageName) {
        this.packageName = packageName;
        return this;
    }

    @Override
    protected void processIOException(IOException e) {
        if (null != e && e instanceof GooglePlayException && ((GooglePlayException) e).getCode() == 404) {
            toast(this.context, R.string.details_not_available_on_play_store);
        }
    }

    @Override
    protected Throwable doInBackground(String... params) {
        PlayStoreApiWrapper wrapper = new PlayStoreApiWrapper(this.context);
        try {
            app = wrapper.getDetails(packageName);
        } catch (Throwable e) {
            return e;
        }
        try {
            app.getPackageInfo().applicationInfo = context.getPackageManager().getApplicationInfo(packageName, 0);
            app.setInstalled(true);
        } catch (PackageManager.NameNotFoundException e) {
            // App is not installed
        }
        return null;
    }
}
