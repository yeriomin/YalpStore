package com.github.yeriomin.yalpstore;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

public class DetailsInstallReceiver extends BroadcastReceiver {

    static public final String ACTION_PACKAGE_REPLACED_NON_SYSTEM = "ACTION_PACKAGE_REPLACED_NON_SYSTEM";

    private Button buttonUninstall;
    private Button buttonInstall;

    public void setButtonInstall(Button buttonInstall) {
        this.buttonInstall = buttonInstall;
    }

    public void setButtonUninstall(Button buttonUninstall) {
        this.buttonUninstall = buttonUninstall;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        buttonUninstall.setVisibility(View.VISIBLE);
        buttonInstall.setEnabled(true);
        buttonInstall.setText(R.string.details_install);
    }
}
