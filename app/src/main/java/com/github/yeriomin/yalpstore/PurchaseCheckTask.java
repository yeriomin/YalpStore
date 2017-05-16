package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;

import com.github.yeriomin.playstoreapi.AndroidAppDeliveryData;
import com.github.yeriomin.yalpstore.model.App;

import java.io.IOException;

public class PurchaseCheckTask extends AsyncTask<Void, Void, AndroidAppDeliveryData> {

    private Context context;
    private App app;
    private DownloadOrInstallFragment downloadOrInstallManager;
    private Button downloadButton;

    public PurchaseCheckTask(Context context, App app, DownloadOrInstallFragment downloadOrInstallManager) {
        this.context = context;
        this.app = app;
        this.downloadOrInstallManager = downloadOrInstallManager;
    }

    public void setDownloadButton(Button downloadButton) {
        this.downloadButton = downloadButton;
    }

    @Override
    protected AndroidAppDeliveryData doInBackground(Void... params) {
        PlayStoreApiWrapper wrapper = new PlayStoreApiWrapper(this.context);
        try {
            return wrapper.purchaseOrDeliver(app);
        } catch (IOException e) {
            // Since Play Store returns 403 error on an attempt to download a non-existing version,
            // we'll cannot use GoogleApiAsyncTask used for all the other requests
            Log.w(getClass().getName(), e.getClass().getName() + " " + e.getMessage());
        } catch (NotPurchasedException e) {
            // Unlikely
        }
        return null;
    }

    @Override
    protected void onPostExecute(AndroidAppDeliveryData androidAppDeliveryData) {
        boolean success = null != androidAppDeliveryData;
        downloadOrInstallManager.draw();
        if (null == downloadButton) {
            return;
        }
        downloadButton.setText(success ? R.string.details_download : R.string.details_download_not_available);
        downloadButton.setEnabled(success);
    }
}
