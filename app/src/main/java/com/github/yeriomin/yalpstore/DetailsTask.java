package com.github.yeriomin.yalpstore;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.github.yeriomin.yalpstore.model.App;

public class DetailsTask extends GoogleApiAsyncTask {

    protected App app;
    protected String packageName;

    public DetailsTask setPackageName(String packageName) {
        this.packageName = packageName;
        return this;
    }

    @Override
    protected Throwable doInBackground(String... params) {
        PlayStoreApiWrapper wrapper = new PlayStoreApiWrapper(this.context);
        try {
            this.app = wrapper.getDetails(packageName);
        } catch (Throwable e) {
            return e;
        }
        Drawable icon;
        try {
            PackageManager pm = this.context.getPackageManager();
            ApplicationInfo installedApp = pm.getApplicationInfo(packageName, 0);
            icon = pm.getApplicationIcon(installedApp);
            this.app.setInstalled(true);
        } catch (PackageManager.NameNotFoundException e) {
            BitmapManager manager = new BitmapManager(this.context);
            icon = null == app.getIconUrl()
                ? this.context.getResources().getDrawable(R.drawable.ic_placeholder)
                : new BitmapDrawable(manager.getBitmap(app.getIconUrl()))
            ;
        }
        this.app.setIcon(icon);
        return null;
    }
}
