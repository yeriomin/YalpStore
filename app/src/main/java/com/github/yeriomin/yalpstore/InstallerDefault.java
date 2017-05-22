package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.util.Log;

import com.github.yeriomin.yalpstore.model.App;

public class InstallerDefault extends InstallerAbstract {

    public InstallerDefault(Context context) {
        super(context);
    }

    @Override
    public void install(App app) {
        if (background) {
            Log.i(getClass().getName(), "Background installation is not supported by default installer");
            return;
        }
        context.startActivity(
            InstallerAbstract.getOpenApkIntent(
                context,
                Downloader.getApkPath(app.getPackageName(), app.getVersionCode())
            )
        );
    }
}
