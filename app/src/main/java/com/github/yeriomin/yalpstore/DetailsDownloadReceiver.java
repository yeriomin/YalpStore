package com.github.yeriomin.yalpstore;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

public class DetailsDownloadReceiver extends BroadcastReceiver {

    private Button buttonDownload;
    private Button buttonInstall;
    private ProgressBar progressBar;

    public DetailsDownloadReceiver(DetailsActivity activity) {
        buttonDownload = (Button) activity.findViewById(R.id.download);
        buttonInstall = (Button) activity.findViewById(R.id.install);
        progressBar = (ProgressBar) activity.findViewById(R.id.download_progress);
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
        draw(context, state);
    }

    private void draw(Context context, DownloadState state) {
        if (null != progressBar) {
            progressBar.setVisibility(View.GONE);
        }
        buttonDownload.setText(R.string.details_download);
        buttonDownload.setEnabled(true);
        if (!state.isEverythingSuccessful()) {
            return;
        }
        buttonDownload.setVisibility(View.GONE);
        buttonInstall.setVisibility(View.VISIBLE);
        if (PreferenceActivity.getBoolean(context, PreferenceActivity.PREFERENCE_AUTO_INSTALL)
            && !state.getTriggeredBy().equals(DownloadState.TriggeredBy.MANUAL_DOWNLOAD_BUTTON)
        ) {
            buttonInstall.setEnabled(false);
            buttonInstall.setText(R.string.details_installing);
        } else {
            buttonInstall.setEnabled(true);
            buttonInstall.setText(R.string.details_install);
        }
    }
}
