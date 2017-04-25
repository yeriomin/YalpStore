package com.github.yeriomin.yalpstore;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DetailsDownloadReceiver extends BroadcastReceiver {

    private Button buttonDownload;
    private Button buttonInstall;

    public DetailsDownloadReceiver(DetailsActivity activity) {
        buttonDownload = (Button) activity.findViewById(R.id.download);
        buttonInstall = (Button) activity.findViewById(R.id.install);
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadManagerInterface.ACTION_DOWNLOAD_COMPLETE);
        activity.registerReceiver(this, filter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        if (null == extras) {
            return;
        }
        long id = extras.getLong(DownloadManagerInterface.EXTRA_DOWNLOAD_ID);
        DownloadState state = DownloadState.get(id);
        if (null == state) {
            return;
        }
        state.setFinished(id);
        if (DownloadManagerFactory.get(context).success(id)) {
            state.setSuccessful(id);
        }
        if (!state.isEverythingFinished()) {
            return;
        }
        buttonDownload.setText(R.string.details_download);
        buttonDownload.setEnabled(true);
        if (state.isEverythingSuccessful()) {
            buttonDownload.setVisibility(View.GONE);
            buttonInstall.setVisibility(View.VISIBLE);
        }
    }
}
