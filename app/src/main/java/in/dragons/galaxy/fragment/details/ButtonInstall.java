package in.dragons.galaxy.fragment.details;

import android.app.NotificationManager;
import android.content.Context;
import android.view.View;

import in.dragons.galaxy.InstallationState;
import in.dragons.galaxy.InstallerFactory;
import in.dragons.galaxy.Paths;
import in.dragons.galaxy.R;
import in.dragons.galaxy.activities.GalaxyActivity;
import in.dragons.galaxy.downloader.DownloadState;
import in.dragons.galaxy.model.App;

public class ButtonInstall extends Button {

    ButtonInstall(GalaxyActivity activity, App app) {
        super(activity, app);
    }

    @Override
    public void draw() {
        super.draw();
        ((android.widget.Button) button).setText(R.string.details_install);
        if (InstallationState.isInstalling(app.getPackageName())) {
            disable(R.string.details_installing);
        }
    }

    @Override
    protected android.widget.Button getButton() {
        return (android.widget.Button) activity.findViewById(R.id.install);
    }

    @Override
    protected boolean shouldBeVisible() {
        return Paths.getApkPath(activity, app.getPackageName(), app.getVersionCode()).exists()
                && DownloadState.get(app.getPackageName()).isEverythingSuccessful()
                ;
    }

    @Override
    protected void onButtonClick(View v) {
        disable(R.string.details_installing);
        ((NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(app.getDisplayName().hashCode());
        InstallerFactory.get(activity).verifyAndInstall(app);
    }
}
