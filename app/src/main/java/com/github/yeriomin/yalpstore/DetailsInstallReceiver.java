package com.github.yeriomin.yalpstore;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class DetailsInstallReceiver extends BroadcastReceiver {

    static public final String ACTION_PACKAGE_REPLACED_NON_SYSTEM = "ACTION_PACKAGE_REPLACED_NON_SYSTEM";

    private ImageButton buttonUninstall;
    private Button buttonInstall;
    private Button buttonRun;
    private boolean deleteApk;

    public DetailsInstallReceiver(DetailsActivity activity) {
        buttonRun = (Button) activity.findViewById(R.id.run);
        buttonInstall = (Button) activity.findViewById(R.id.install);
        buttonUninstall = (ImageButton) activity.findViewById(R.id.uninstall);
        deleteApk = PreferenceActivity.getBoolean(activity, PreferenceActivity.PREFERENCE_DELETE_APK_AFTER_INSTALL);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_INSTALL);
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        filter.addAction(DetailsInstallReceiver.ACTION_PACKAGE_REPLACED_NON_SYSTEM);
        activity.registerReceiver(this, filter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (null != buttonRun) {
            buttonRun.setVisibility(View.VISIBLE);
        }
        if (null != buttonUninstall) {
            buttonUninstall.setVisibility(View.VISIBLE);
        }
        buttonInstall.setVisibility(deleteApk ? View.GONE : View.VISIBLE);
    }
}
