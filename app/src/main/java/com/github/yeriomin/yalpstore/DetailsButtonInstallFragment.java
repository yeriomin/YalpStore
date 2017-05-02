package com.github.yeriomin.yalpstore;

import android.app.NotificationManager;
import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.github.yeriomin.yalpstore.model.App;

public class DetailsButtonInstallFragment extends DetailsButtonFragment {

    public DetailsButtonInstallFragment(DetailsActivity activity, App app) {
        super(activity, app);
    }

    @Override
    public void draw() {
        super.draw();
        ((Button) button).setText(R.string.details_install);
    }

    @Override
    protected Button getButton() {
        return (Button) activity.findViewById(R.id.install);
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
                InstallerFactory.get(activity).install(app);
            }
        };
    }
}
