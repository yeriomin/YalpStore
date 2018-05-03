package com.dragons.aurora.fragment.details;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.dragons.aurora.R;
import com.dragons.aurora.activities.AuroraActivity;
import com.dragons.aurora.model.App;
import com.dragons.aurora.task.CheckShellTask;
import com.dragons.aurora.task.UninstallSystemAppTask;

public class ButtonUninstall extends Button {

    public ButtonUninstall(AuroraActivity activity, App app) {
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
        if (isSystemAndReadyForPermanentUninstall()) {
            askAndUninstall();
        } else {
            activity.startActivity(new Intent(Intent.ACTION_DELETE, Uri.parse("package:" + app.getPackageName())));
        }
        View buttonRun = activity.findViewById(R.id.run);
        if (buttonRun != null)
            buttonRun.setVisibility(View.GONE);
    }

    private boolean isSystemAndReadyForPermanentUninstall() {
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
