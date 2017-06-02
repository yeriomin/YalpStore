package com.github.yeriomin.yalpstore;

import android.app.Activity;
import android.content.Context;

public class InstallerFactory {

    static public InstallerAbstract get(Context context) {
        if (context instanceof Activity) {
            return new InstallerDefault(context);
        }
        String userChoice = PreferenceActivity.getString(context, PreferenceActivity.PREFERENCE_INSTALLATION_METHOD);
        switch (userChoice) {
            case PreferenceActivity.INSTALLATION_METHOD_PRIVILEGED:
                return new InstallerPrivileged(context);
            case PreferenceActivity.INSTALLATION_METHOD_ROOT:
                return new InstallerRoot(context);
            case PreferenceActivity.INSTALLATION_METHOD_DEFAULT:
            default:
                return new InstallerDefault(context);
        }
    }
}
