package in.dragons.galaxy;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class FirstLaunchChecker {

    static private final String FIRST_LOGIN = "FIRST_LOGIN";

    private SharedPreferences prefs;

    public FirstLaunchChecker(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean isFirstLogin() {
        return prefs.getBoolean(FIRST_LOGIN, true);
    }

    public void setLoggedIn() {
        prefs.edit()
            .putBoolean(FIRST_LOGIN, false)
            .commit()
        ;
    }
}
