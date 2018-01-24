package in.dragons.galaxy;

import android.content.Context;

public class InstallerFactory {

    static public InstallerAbstract get(Context context) {
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
