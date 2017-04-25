package com.github.yeriomin.yalpstore;

import com.github.yeriomin.yalpstore.model.App;

public class DownloadOrInstallFragment extends DetailsFragment {

    private DetailsDownloadReceiver downloadReceiver;
    private DetailsInstallReceiver installReceiver;

    public DownloadOrInstallFragment(DetailsActivity activity, App app) {
        super(activity, app);
    }

    @Override
    public void draw() {
        new DetailsButtonUninstallFragment(activity, app).draw();
        new DetailsButtonDownloadFragment(activity, app).draw();
        new DetailsButtonInstallFragment(activity, app).draw();
        new DetailsButtonRunFragment(activity, app).draw();
    }

    public void download() {
        new DetailsButtonDownloadFragment(activity, app).download();
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
