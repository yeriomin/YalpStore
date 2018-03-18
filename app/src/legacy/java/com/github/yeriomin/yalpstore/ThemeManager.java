package com.github.yeriomin.yalpstore;

import android.os.Build;

public class ThemeManager extends ThemeManagerAbstract {

    protected int getThemeLight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return android.R.style.Theme_Material_Light;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return android.R.style.Theme_Holo_Light;
        } else {
            return android.R.style.Theme_Light;
        }
    }

    protected int getThemeDark() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return android.R.style.Theme_Material;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return android.R.style.Theme_Holo;
        } else {
            return android.R.style.Theme;
        }
    }

    @Override
    protected int getDialogThemeLight() {
        return 0;
    }

    @Override
    protected int getDialogThemeDark() {
        return 0;
    }
}
