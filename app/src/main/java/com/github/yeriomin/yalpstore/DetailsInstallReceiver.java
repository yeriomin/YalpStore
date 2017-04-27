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

    public DetailsInstallReceiver(DetailsActivity activity) {
        buttonInstall = (Button) activity.findViewById(R.id.install);
        buttonUninstall = (ImageButton) activity.findViewById(R.id.uninstall);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_INSTALL);
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        filter.addAction(DetailsInstallReceiver.ACTION_PACKAGE_REPLACED_NON_SYSTEM);
        activity.registerReceiver(this, filter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        buttonUninstall.setVisibility(View.VISIBLE);
        buttonInstall.setEnabled(true);
        buttonInstall.setText(R.string.details_install);
    }
}
