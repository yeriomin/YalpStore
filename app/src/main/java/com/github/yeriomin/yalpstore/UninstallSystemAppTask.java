package com.github.yeriomin.yalpstore;

import android.content.Context;

import com.github.yeriomin.yalpstore.model.App;

import java.util.ArrayList;
import java.util.List;

public class UninstallSystemAppTask extends SystemRemountTask {

    public UninstallSystemAppTask(Context context, App app) {
        super(context, app);
    }

    @Override
    protected List<String> getCommands() {
        List<String> commands = new ArrayList<>();
        String from = app.getPackageInfo().applicationInfo.sourceDir;
        commands.add(getBusyboxCommand("rm -rf " + from));
        return commands;
    }
}
