package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.github.yeriomin.yalpstore.model.App;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PermissionsComparator {

    private Context context;

    public PermissionsComparator(Context context) {
        this.context = context;
    }

    public boolean isSame(App app) {
        Log.i(getClass().getName(), "Checking " + app.getPackageName());
        Set<String> oldPermissions = getOldPermissions(app.getPackageName());
        if (null == oldPermissions) {
            return true;
        }
        Set<String> newPermissions = new HashSet<>(app.getPermissions());
        newPermissions.removeAll(oldPermissions);
        Log.i(getClass().getName(), app.getPackageName() + " requests " + newPermissions.size() + " new permissions");
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
            Log.e(getClass().getName(), "Package " + packageName + " doesn't seem to be installed");
        }
        return null;
    }
}
