package com.dragons.aurora.fragment.details;

import android.content.Intent;
import android.view.View;

import com.percolate.caffeine.ViewUtils;

import com.dragons.aurora.NumberProgressBar;
import com.dragons.aurora.R;
import com.dragons.aurora.activities.AuroraActivity;
import com.dragons.aurora.downloader.DownloadState;
import com.dragons.aurora.model.App;
import com.dragons.aurora.notification.CancelDownloadService;

public class ButtonCancel extends Button {

    ButtonCancel(AuroraActivity activity, App app) {
        super(activity, app);
    }

    @Override
    protected android.widget.Button getButton() {
        return (android.widget.Button) activity.findViewById(R.id.cancel);
    }

    @Override
    protected boolean shouldBeVisible() {
        return !DownloadState.get(app.getPackageName()).isEverythingFinished();
    }

    @Override
    protected void onButtonClick(View button) {
        Intent intentCancel = new Intent(activity.getApplicationContext(), CancelDownloadService.class);
        intentCancel.putExtra(CancelDownloadService.PACKAGE_NAME, app.getPackageName());
        activity.startService(intentCancel);
        button.setVisibility(View.GONE);

        android.widget.Button buttonDownload = ViewUtils.findViewById(activity, R.id.download);
        NumberProgressBar numberProgressBar = ViewUtils.findViewById(activity, R.id.download_progress);

        buttonDownload.setVisibility(View.VISIBLE);
        buttonDownload.setEnabled(true);
        numberProgressBar.setProgress(0);
        numberProgressBar.setVisibility(View.GONE);
    }
}
