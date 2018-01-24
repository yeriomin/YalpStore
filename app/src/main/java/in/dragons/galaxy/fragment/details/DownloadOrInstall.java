package in.dragons.galaxy.fragment.details;

import in.dragons.galaxy.DetailsActivity;
import in.dragons.galaxy.DetailsDownloadReceiver;
import in.dragons.galaxy.DetailsInstallReceiver;
import in.dragons.galaxy.model.App;

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
        new ButtonCancel((DetailsActivity) activity, app).draw();
        new ButtonInstall((DetailsActivity) activity, app).draw();
        new ButtonRun((DetailsActivity) activity, app).draw();
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
