package in.dragons.galaxy.task.playstore;

import android.view.View;
import android.widget.Button;

import com.github.yeriomin.playstoreapi.AndroidAppDeliveryData;

import java.util.Timer;

import in.dragons.galaxy.R;
import in.dragons.galaxy.fragment.details.DownloadOrInstall;

public class PurchaseCheckTask extends DeliveryDataTask {

    private DownloadOrInstall downloadOrInstallFragment;
    private Button downloadButton;
    private Timer timer;

    public void setDownloadOrInstallFragment(DownloadOrInstall downloadOrInstallFragment) {
        this.downloadOrInstallFragment = downloadOrInstallFragment;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public void setDownloadButton(Button downloadButton) {
        this.downloadButton = downloadButton;
    }

    @Override
    protected void onPostExecute(AndroidAppDeliveryData deliveryData) {
        boolean success = success() && null != deliveryData;
        downloadOrInstallFragment.draw();
        if (null == downloadButton) {
            return;
        }
        downloadButton.setText(success ? R.string.details_download : R.string.details_download_not_available);
        downloadButton.setEnabled(success);
        downloadButton.setVisibility(View.VISIBLE);
        timer.cancel();
    }
}
