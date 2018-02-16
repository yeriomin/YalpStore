package in.dragons.galaxy.task;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import in.dragons.galaxy.model.App;

public class UninstallSystemAppTask extends SystemRemountTask {

    public UninstallSystemAppTask(Context context, App app) {
        super(context, app);
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
