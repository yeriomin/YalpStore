package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.util.Log;

import com.github.yeriomin.yalpstore.model.App;

public class InstallerDefault extends InstallerAbstract {

    public InstallerDefault(Context context) {
        super(context);
    }

    @Override
    public boolean verify(App app) {
        if (background) {
            Log.i(getClass().getSimpleName(), "Background installation is not supported by default installer");
            return false;
        }
        return super.verify(app);
    }

    @Override
    protected void install(App app) {
        context.startActivity(
            InstallerAbstract.getOpenApkIntent(
                context,
                Paths.getApkPath(context, app.getPackageName(), app.getVersionCode())
            )
        );
    }
}
