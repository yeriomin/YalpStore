package com.github.yeriomin.yalpstore.view;

import android.view.View;

public abstract class ButtonAdapter {

    protected View button;

    public ButtonAdapter(View button) {
        this.button = button;
    }

    public ButtonAdapter show() {
        button.setVisibility(View.VISIBLE);
        return this;
    }

    public ButtonAdapter hide() {
        button.setVisibility(View.GONE);
        return this;
    }

    public ButtonAdapter enable() {
        button.setEnabled(true);
        return this;
    }

    public ButtonAdapter disable() {
        button.setEnabled(false);
        return this;
    }
}
