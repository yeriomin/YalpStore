package com.dragons.aurora.task.playstore;

import android.view.View;
import android.widget.Button;

import com.dragons.aurora.R;
import com.dragons.aurora.fragment.details.DownloadOrInstall;
import com.dragons.aurora.playstoreapiv2.AndroidAppDeliveryData;

import java.util.Timer;

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
