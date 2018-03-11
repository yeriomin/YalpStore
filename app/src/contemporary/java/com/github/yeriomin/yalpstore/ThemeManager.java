package com.github.yeriomin.yalpstore;

import android.app.Activity;

public class ThemeManager extends ThemeManagerAbstract {

    public ThemeManager(Activity activity) {
        super(activity);
    }

    protected int getThemeLight() {
        return R.style.YalpStoreThemeLight;
    }

    protected int getThemeDark() {
        return R.style.YalpStoreThemeDark;
    }
}
