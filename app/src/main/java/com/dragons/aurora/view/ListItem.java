package com.dragons.aurora.view;

import android.view.View;

public abstract class ListItem {

    protected View view;

    public void setView(View view) {
        this.view = view;
    }

    abstract public void draw();
}
