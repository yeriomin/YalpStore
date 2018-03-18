package com.github.yeriomin.yalpstore;

public class ThemeManager extends ThemeManagerAbstract {

    protected int getThemeLight() {
        return R.style.YalpStoreThemeLight;
    }

    protected int getThemeDark() {
        return R.style.YalpStoreThemeDark;
    }

    @Override
    protected int getThemeBlack() {
        return R.style.YalpStoreThemeBlack;
    }

    @Override
    protected int getDialogThemeLight() {
        return R.style.YalpStoreDialogStyleLight;
    }

    @Override
    protected int getDialogThemeDark() {
        return R.style.YalpStoreDialogStyleDark;
    }
}
