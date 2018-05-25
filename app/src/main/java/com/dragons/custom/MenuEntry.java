package com.dragons.custom;

import android.graphics.drawable.Drawable;

class MenuEntry {

    private String title;
    private Drawable icon;
    private int resId;

    MenuEntry(String title, Drawable icon, int resId) {
        this.title = title;
        this.icon = icon;
        this.resId = resId;
    }

    public String getTitle() {
        return title;
    }

    public Drawable getIcon() {
        return icon;
    }

    public int getResId() {
        return resId;
    }
}
