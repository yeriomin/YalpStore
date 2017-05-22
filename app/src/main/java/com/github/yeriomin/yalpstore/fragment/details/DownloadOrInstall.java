package com.github.yeriomin.yalpstore.fragment.details;

import com.github.yeriomin.yalpstore.DetailsActivity;
import com.github.yeriomin.yalpstore.DetailsDownloadReceiver;
import com.github.yeriomin.yalpstore.DetailsInstallReceiver;
import com.github.yeriomin.yalpstore.model.App;

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
        new ButtonInstall(activity, app).draw();
        new ButtonRun(activity, app).draw();
    }

    public void download() {
        new ButtonDownload(activity, app).download();
    }

    public void unregisterReceivers() {
        if (null != downloadReceiver) {
            activity.unregisterReceiver(downloadReceiver);
            downloadReceiver = null;
        }
        if (null != installReceiver) {
            activity.unregisterReceiver(installReceiver);
            installReceiver = null;
        }
    }

    public void registerReceivers() {
        if (null == downloadReceiver) {
            downloadReceiver = new DetailsDownloadReceiver(activity);
        }
        if (null == installReceiver) {
            installReceiver = new DetailsInstallReceiver(activity);
        }
    }
}
