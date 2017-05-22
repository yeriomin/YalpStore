package com.github.yeriomin.yalpstore;

import android.content.Context;

import com.github.yeriomin.yalpstore.model.App;

public class InstallerRoot extends InstallerAbstract {

    public InstallerRoot(Context context) {
        super(context);
    }

    @Override
    public void install(App app) {
        new InstallTask(context, app.getDisplayName())
            .execute(Downloader.getApkPath(app.getPackageName(), app.getVersionCode()).toString());
    }
}
