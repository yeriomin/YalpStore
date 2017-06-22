package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.util.Log;

import com.github.yeriomin.yalpstore.model.App;

import java.io.File;

public class InstallerDefault extends InstallerAbstract {

    public InstallerDefault(Context context) {
        super(context);
    }

    @Override
    public void verifyAndInstall(App app) {
        if (background) {
            Log.i(getClass().getName(), "Background installation is not supported by default installer");
            return;
        }
        File file = Downloader.getApkPath(app.getPackageName(), app.getVersionCode());
        if (!new ApkSignatureVerifier(context).match(app.getPackageName(), file)) {
            Log.i(getClass().getName(), "Signature mismatch for " + app.getPackageName());
            if (Util.isContextUiCapable(context)) {
                getSignatureMismatchDialog().show();
            } else {
                notifySignatureMismatch(app);
            }
        } else {
            Log.i(getClass().getName(), "Installing " + app.getPackageName());
            install(app);
        }
    }

    @Override
    protected void install(App app) {
        context.startActivity(
            InstallerAbstract.getOpenApkIntent(
                context,
                Downloader.getApkPath(app.getPackageName(), app.getVersionCode())
            )
        );
    }
}
