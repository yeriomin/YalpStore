package com.github.yeriomin.yalpstore;

import android.view.View;
import android.widget.Button;

import com.github.yeriomin.playstoreapi.AuthException;
import com.github.yeriomin.yalpstore.fragment.details.DownloadOrInstall;
import com.github.yeriomin.yalpstore.model.App;

import java.io.IOException;
import java.util.Timer;

public class PurchaseCheckTask extends GoogleApiAsyncTask {

    private App app;
    private DownloadOrInstall downloadOrInstallFragment;
    private Button downloadButton;
    private Timer timer;

    public void setApp(App app) {
        this.app = app;
    }

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
    protected Throwable doInBackground(String... params) {
        try {
            new PlayStoreApiWrapper(context).purchase(app);
        } catch (IOException e) {
            return e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Throwable e) {
        boolean success = null == e;
        downloadOrInstallFragment.draw();
        if (null == downloadButton) {
            return;
        }
        downloadButton.setText(success ? R.string.details_download : R.string.details_download_not_available);
        downloadButton.setEnabled(success);
        downloadButton.setVisibility(View.VISIBLE);
        timer.cancel();
    }

    @Override
    protected void processAuthException(AuthException e) {
        // Since Play Store returns 403 error on an attempt to download a non-existing version,
        // we need to ignore it
    }
}
