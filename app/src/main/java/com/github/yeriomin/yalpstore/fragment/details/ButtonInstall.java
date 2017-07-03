package com.github.yeriomin.yalpstore.fragment.details;

import android.app.NotificationManager;
import android.content.Context;
import android.view.View;

import com.github.yeriomin.yalpstore.DetailsActivity;
import com.github.yeriomin.yalpstore.DownloadState;
import com.github.yeriomin.yalpstore.InstallerFactory;
import com.github.yeriomin.yalpstore.Paths;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.model.App;

public class ButtonInstall extends Button {

    public ButtonInstall(DetailsActivity activity, App app) {
        super(activity, app);
    }

    @Override
    public void draw() {
        super.draw();
        ((android.widget.Button) button).setText(R.string.details_install);
    }

    @Override
    protected android.widget.Button getButton() {
        return (android.widget.Button) activity.findViewById(R.id.install);
    }

    @Override
    protected boolean shouldBeVisible() {
        return Paths.getApkPath(app.getPackageName(), app.getVersionCode()).exists()
            && DownloadState.get(app.getPackageName()).isEverythingSuccessful()
        ;
    }

    @Override
    protected void onButtonClick(View v) {
        disableButton(R.id.install, R.string.details_installing);
        ((NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(app.getDisplayName().hashCode());
        InstallerFactory.get(activity).verifyAndInstall(app);
    }
}
