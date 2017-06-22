package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.content.Intent;

import com.github.yeriomin.yalpstore.model.App;

public class InstallerRoot extends InstallerBackground {

    public InstallerRoot(Context context) {
        super(context);
    }

    @Override
    protected void install(App app) {
        getTask(app).execute(Downloader.getApkPath(app.getPackageName(), app.getVersionCode()).toString());
    }

    private InstallTask getTask(final App app) {
        return new InstallTask() {

            @Override
            protected void onPostExecute(Boolean success) {
                super.onPostExecute(success);
                context.sendBroadcast(new Intent(DetailsInstallReceiver.ACTION_PACKAGE_REPLACED_NON_SYSTEM));
                postInstallationResult(app, success);
            }
        };
    }
}
