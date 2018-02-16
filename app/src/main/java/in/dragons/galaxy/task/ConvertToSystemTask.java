package in.dragons.galaxy.task;

import android.content.Context;
import android.os.Build;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import in.dragons.galaxy.model.App;

public class ConvertToSystemTask extends SystemRemountTask {

    public ConvertToSystemTask(Context context, App app) {
        super(context, app);
    }

    @Override
    protected List<String> getCommands() {
        List<String> commands = new ArrayList<>();
        String from = app.getPackageInfo().applicationInfo.sourceDir;
        String targetPath = getTargetPath();
        String targetDir = new File(targetPath).getParent();
        commands.add(getBusyboxCommand("mkdir " + targetDir));
        commands.add(getBusyboxCommand("chmod 755 " + targetDir));
        commands.add(getBusyboxCommand("mv " + from + " " + targetPath));
        commands.add(getBusyboxCommand("chmod 644 " + targetPath));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            commands.add(getBusyboxCommand("chown system " + targetPath));
            commands.add(getBusyboxCommand("chgrp system " + targetPath));
        }
        return commands;
    }

    private String getTargetPath() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return "/system/priv-app/" + app.getPackageName() + "/" + app.getPackageName() + ".apk";
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return "/system/priv-app/" + app.getPackageName() + ".apk";
        } else {
            return "/system/app/" + app.getPackageName() + ".apk";
        }
    }
}
