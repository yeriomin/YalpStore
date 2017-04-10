package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class FirstLaunchChecker {

    static private final String FIRST_LAUNCH = "FIRST_LAUNCH";

    private SharedPreferences prefs;

    public FirstLaunchChecker(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean isFirstLaunch() {
        return prefs.getBoolean(FIRST_LAUNCH, true);
    }

    public void setLaunched() {
        prefs.edit()
            .putBoolean(FIRST_LAUNCH, false)
            .commit()
        ;
    }
}
