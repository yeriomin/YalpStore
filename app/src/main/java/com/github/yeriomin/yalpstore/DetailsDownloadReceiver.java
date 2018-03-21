package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

public class DetailsDownloadReceiver extends ForegroundDownloadReceiver {

    private String packageName;

    public DetailsDownloadReceiver(DetailsActivity activity, String packageName) {
        super(activity);
        this.packageName = packageName;
    }

    @Override
    protected void process(Context context, Intent intent) {
        if (!state.getApp().getPackageName().equals(packageName)) {
            return;
        }
        super.process(context, intent);
    }

    protected void draw() {
        cleanup();
        if (!state.isEverythingSuccessful()) {
            return;
        }
        activityRef.get().findViewById(R.id.download).setVisibility(View.GONE);
        activityRef.get().findViewById(R.id.install).setVisibility(View.VISIBLE);
        boolean installing = !state.getTriggeredBy().equals(DownloadState.TriggeredBy.MANUAL_DOWNLOAD_BUTTON)
            && (PreferenceUtil.getBoolean(activityRef.get(), PreferenceUtil.PREFERENCE_AUTO_INSTALL)
                || PreferenceUtil.getBoolean(activityRef.get(), PreferenceUtil.PREFERENCE_DOWNLOAD_INTERNAL_STORAGE)
            )
        ;
        toggle(R.id.install, installing ? R.string.details_installing : R.string.details_install, !installing);
    }

    protected void cleanup() {
        ProgressBar progressBar = activityRef.get().findViewById(R.id.download_progress);
        if (null != progressBar) {
            progressBar.setVisibility(View.INVISIBLE);
            progressBar.setProgress(0);
        }
        View buttonCancel = activityRef.get().findViewById(R.id.cancel);
        if (null != buttonCancel) {
            buttonCancel.setVisibility(View.GONE);
        }
        toggle(R.id.download, R.string.details_download, true);
    }

    private void toggle(int buttonId, int stringResId, boolean enable) {
        View button = activityRef.get().findViewById(buttonId);
        if (null == button) {
            return;
        }
        button.setEnabled(enable);
        if (button instanceof Button) {
            ((Button) button).setText(stringResId);
        }
    }
}
