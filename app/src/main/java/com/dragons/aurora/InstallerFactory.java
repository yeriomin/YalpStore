package com.dragons.aurora;

import android.content.Context;

import com.dragons.aurora.fragment.PreferenceFragment;

public class InstallerFactory {

    static public InstallerAbstract get(Context context) {
        String userChoice = PreferenceFragment.getString(context, PreferenceFragment.PREFERENCE_INSTALLATION_METHOD);
        switch (userChoice) {
            case PreferenceFragment.INSTALLATION_METHOD_PRIVILEGED:
                return new InstallerPrivileged(context);
            case PreferenceFragment.INSTALLATION_METHOD_ROOT:
                return new InstallerRoot(context);
            case PreferenceFragment.INSTALLATION_METHOD_DEFAULT:
            default:
                return new InstallerDefault(context);
        }
    }
}
