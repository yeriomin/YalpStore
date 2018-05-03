package com.dragons.aurora.fragment.details;

import android.content.Intent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dragons.aurora.R;
import com.dragons.aurora.activities.AuroraActivity;
import com.dragons.aurora.downloader.DownloadState;
import com.dragons.aurora.model.App;
import com.dragons.aurora.notification.CancelDownloadService;
import com.percolate.caffeine.ViewUtils;

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
        ProgressBar progressBar = ViewUtils.findViewById(activity, R.id.download_progress);
        TextView progressCents = ViewUtils.findViewById(activity, R.id.progressCents);

        buttonDownload.setVisibility(View.VISIBLE);
        buttonDownload.setEnabled(true);
        progressBar.setProgress(0);
        progressBar.setVisibility(View.GONE);
        progressCents.setVisibility(View.GONE);
    }
}
