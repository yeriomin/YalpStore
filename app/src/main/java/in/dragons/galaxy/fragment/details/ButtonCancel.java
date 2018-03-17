package in.dragons.galaxy.fragment.details;

import android.content.Intent;
import android.view.View;

import com.percolate.caffeine.ViewUtils;

import in.dragons.galaxy.downloader.DownloadState;
import in.dragons.galaxy.activities.GalaxyActivity;
import in.dragons.galaxy.R;
import in.dragons.galaxy.model.App;
import in.dragons.galaxy.notification.CancelDownloadService;

public class ButtonCancel extends Button {

    ButtonCancel(GalaxyActivity activity, App app) {
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
        buttonDownload.setVisibility(View.VISIBLE);
        buttonDownload.setEnabled(true);
    }
}
