package com.github.yeriomin.yalpstore.task;

import android.app.Activity;

import com.github.yeriomin.yalpstore.model.App;

import java.util.ArrayList;
import java.util.List;

public class ConvertToNormalTask extends SystemRemountTask {

    public ConvertToNormalTask(Activity activity, App app) {
        super(activity, app);
    }

    @Override
    protected List<String> getCommands() {
        List<String> commands = new ArrayList<>();
        String from = app.getPackageInfo().applicationInfo.sourceDir;
        commands.add(getBusyboxCommand("mv " + from + " /data/app"));
        return commands;
    }
}
