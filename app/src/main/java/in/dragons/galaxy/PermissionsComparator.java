package in.dragons.galaxy;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import in.dragons.galaxy.model.App;

public class PermissionsComparator {

    private Context context;

    public PermissionsComparator(Context context) {
        this.context = context;
    }

    public boolean isSame(App app) {
        Log.i(getClass().getSimpleName(), "Checking " + app.getPackageName());
        Set<String> oldPermissions = getOldPermissions(app.getPackageName());
        if (null == oldPermissions) {
            return true;
        }
        Set<String> newPermissions = new HashSet<>(app.getPermissions());
        newPermissions.removeAll(oldPermissions);
        Log.i(
                getClass().getSimpleName(),
                newPermissions.isEmpty()
                        ? app.getPackageName() + " requests no new permissions"
                        : app.getPackageName() + " requests new permissions: " + TextUtils.join(", ", newPermissions)
        );
        return newPermissions.isEmpty();
    }

    private Set<String> getOldPermissions(String packageName) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            return new HashSet<>(Arrays.asList(
                    null == pi.requestedPermissions
                            ? new String[0]
                            : pi.requestedPermissions
            ));
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(getClass().getSimpleName(), "Package " + packageName + " doesn't seem to be installed");
        }
        return null;
    }
}
