package com.github.yeriomin.yalpstore.task;

import android.app.Activity;
import android.content.Context;

import com.github.yeriomin.yalpstore.model.App;

import java.util.ArrayList;
import java.util.List;

public class UninstallSystemAppTask extends SystemRemountTask {

    public UninstallSystemAppTask(Activity activity, App app) {
        super(activity, app);
    }

    @Override
    protected List<String> getCommands() {
        List<String> commands = new ArrayList<>();
        String from = app.getPackageInfo().applicationInfo.sourceDir;
        commands.add("am force-stop " + app.getPackageName());
        commands.add(getBusyboxCommand("rm -rf " + from));
        return commands;
    }

    @Override
    protected void onPostExecute(List<String> output) {
        super.onPostExecute(output);
        app.setInstalled(false);
    }
}
