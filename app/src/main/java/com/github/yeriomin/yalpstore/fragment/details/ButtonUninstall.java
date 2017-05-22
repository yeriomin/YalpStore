package com.github.yeriomin.yalpstore.fragment.details;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.github.yeriomin.yalpstore.CheckShellTask;
import com.github.yeriomin.yalpstore.DetailsActivity;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.UninstallSystemAppTask;
import com.github.yeriomin.yalpstore.model.App;

public class ButtonUninstall extends Button {

    public ButtonUninstall(DetailsActivity activity, App app) {
        super(activity, app);
    }

    @Override
    protected View getButton() {
        return activity.findViewById(R.id.uninstall);
    }

    @Override
    protected boolean shouldBeVisible() {
        return isInstalled();
    }

    @Override
    protected View.OnClickListener getOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSystemAndReadyForPermanentUninstallation()) {
                    askAndUninstall();
                } else {
                    activity.startActivity(new Intent(Intent.ACTION_DELETE, Uri.parse("package:" + app.getPackageName())));
                }
            }
        };
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
