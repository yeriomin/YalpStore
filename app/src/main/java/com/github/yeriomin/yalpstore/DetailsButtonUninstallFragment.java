package com.github.yeriomin.yalpstore;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.View;

import com.github.yeriomin.yalpstore.model.App;

public class DetailsButtonUninstallFragment extends DetailsButtonFragment {

    public DetailsButtonUninstallFragment(DetailsActivity activity, App app) {
        super(activity, app);
    }

    @Override
    protected View getButton() {
        return activity.findViewById(R.id.uninstall);
    }

    @Override
    protected boolean shouldBeVisible() {
        try {
            activity.getPackageManager().getPackageInfo(app.getPackageName(), 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
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
        return app.isSystem() && app.getPackageInfo().applicationInfo.sourceDir.startsWith("/system/");
    }

    private void askAndUninstall() {
        new SystemRemountDialogBuilder(activity)
            .setPrimaryTask(new UninstallSystemAppTask(activity, app))
            .setMessage(R.string.dialog_message_system_app_self)
            .setTitle(R.string.dialog_title_system_app_self)
            .show()
        ;
    }
}
