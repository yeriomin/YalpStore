package in.dragons.custom;

import android.graphics.drawable.Drawable;

/**
 * Created by Valentin on 12/06/2017.
 */

class MenuEntry {

    private String title;
    private Drawable icon;
    private int resId;

    public MenuEntry(String title, Drawable icon, int resId) {
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
