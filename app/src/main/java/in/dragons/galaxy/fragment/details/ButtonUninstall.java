package in.dragons.galaxy.fragment.details;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

import in.dragons.galaxy.activities.GalaxyActivity;
import in.dragons.galaxy.R;
import in.dragons.galaxy.model.App;
import in.dragons.galaxy.task.CheckShellTask;
import in.dragons.galaxy.task.UninstallSystemAppTask;

public class ButtonUninstall extends Button {

    public ButtonUninstall(GalaxyActivity activity, App app) {
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
