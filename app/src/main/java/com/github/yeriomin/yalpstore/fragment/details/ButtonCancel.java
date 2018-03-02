package com.github.yeriomin.yalpstore.fragment.details;

import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;

import com.github.yeriomin.yalpstore.DetailsActivity;
import com.github.yeriomin.yalpstore.DownloadState;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.notification.CancelDownloadService;

public class ButtonCancel extends Button {

    public ButtonCancel(DetailsActivity activity, App app) {
        super(activity, app);
    }

    @Override
    protected View getButton() {
        return activity.findViewById(R.id.cancel);
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
        View buttonDownload = activity.findViewById(R.id.download);
        if (buttonDownload instanceof android.widget.Button) {
            ((android.widget.Button) buttonDownload).setText(R.string.details_download);
        }
        buttonDownload.setEnabled(true);
    }
}
