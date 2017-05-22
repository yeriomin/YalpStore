package com.github.yeriomin.yalpstore.fragment.details;

import android.app.NotificationManager;
import android.content.Context;
import android.view.View;

import com.github.yeriomin.yalpstore.DetailsActivity;
import com.github.yeriomin.yalpstore.Downloader;
import com.github.yeriomin.yalpstore.InstallerDefault;
import com.github.yeriomin.yalpstore.InstallerFactory;
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
        return Downloader.getApkPath(app.getPackageName(), app.getVersionCode()).exists();
    }

    @Override
    protected View.OnClickListener getOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableButton(R.id.install, R.string.details_installing);
                NotificationManager manager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(app.getDisplayName().hashCode());
                new InstallerDefault(activity).install(app);
            }
        };
    }
}
