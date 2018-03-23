package com.github.yeriomin.yalpstore;

import android.content.Context;

public class InstallerFactory {

    static public InstallerAbstract get(Context context) {
        String userChoice = PreferenceUtil.getString(context, PreferenceUtil.PREFERENCE_INSTALLATION_METHOD);
        switch (userChoice) {
            case PreferenceUtil.INSTALLATION_METHOD_PRIVILEGED:
                return new InstallerPrivileged(context);
            case PreferenceUtil.INSTALLATION_METHOD_ROOT:
                return new InstallerRoot(context);
            case PreferenceUtil.INSTALLATION_METHOD_DEFAULT:
                return new InstallerDefault(context);
            default:
                return YalpStorePermissionManager.hasInstallPermission(context)
                    ? new InstallerPrivileged(context)
                    : new InstallerDefault(context)
                ;
        }
    }
}
