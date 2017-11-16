package com.github.yeriomin.yalpstore.fragment.details;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.YalpStoreActivity;
import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.task.CheckShellTask;
import com.github.yeriomin.yalpstore.task.UninstallSystemAppTask;

public class ButtonUninstall extends Button {

    public ButtonUninstall(YalpStoreActivity activity, App app) {
        super(activity, app);
    }

    @Override
    protected View getButton() {
        return activity.findViewById(R.id.uninstall);
    }

    @Override
    public boolean shouldBeVisible() {
        return isInstalled();
    }

    @Override
    protected void onButtonClick(View v) {
        uninstall();
    }

    public void uninstall() {
        if (isSystemAndReadyForPermanentUninstallation()) {
            askAndUninstall();
        } else {
            activity.startActivity(new Intent(Intent.ACTION_DELETE, Uri.parse("package:" + app.getPackageName())));
        }
    }

    private boolean isSystemAndReadyForPermanentUninstallation() {
        return app.isSystem()
            && null != app.getPackageInfo().applicationInfo
            && null != app.getPackageInfo().applicationInfo.sourceDir
            && app.getPackageInfo().applicationInfo.sourceDir.startsWith("/system/")
        ;
    }

    private void askAndUninstall() {
        CheckShellTask checkShellTask = new CheckShellTask(activity);
        checkShellTask.setPrimaryTask(new UninstallSystemAppTask(activity, app));
        checkShellTask.execute();
    }
}
