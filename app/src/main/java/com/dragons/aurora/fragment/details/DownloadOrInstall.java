package com.dragons.aurora.fragment.details;

import com.dragons.aurora.DetailsDownloadReceiver;
import com.dragons.aurora.DetailsInstallReceiver;
import com.dragons.aurora.activities.AuroraActivity;
import com.dragons.aurora.activities.DetailsActivity;
import com.dragons.aurora.model.App;

public class DownloadOrInstall extends Abstract {

    private DetailsDownloadReceiver downloadReceiver;
    private DetailsInstallReceiver installReceiver;

    public DownloadOrInstall(DetailsActivity activity, App app) {
        super(activity, app);
    }

    @Override
    public void draw() {
        new ButtonUninstall(activity, app).draw();
        new ButtonDownload(activity, app).draw();
        new ButtonCancel(activity, app).draw();
        new ButtonInstall(activity, app).draw();
        new ButtonRun(activity, app).draw();
    }

    public void download() {
        new ButtonDownload(activity, app).download();
    }

    public void unregisterReceivers() {
        activity.unregisterReceiver(downloadReceiver);
        downloadReceiver = null;
        activity.unregisterReceiver(installReceiver);
        installReceiver = null;
    }

    public void registerReceivers() {
        if (null == downloadReceiver) {
            downloadReceiver = new DetailsDownloadReceiver((DetailsActivity) activity, app.getPackageName());
        }
        if (null == installReceiver) {
            installReceiver = new DetailsInstallReceiver((DetailsActivity) activity, app.getPackageName());
        }
    }
}
