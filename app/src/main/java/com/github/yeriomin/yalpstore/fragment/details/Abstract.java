package com.github.yeriomin.yalpstore.fragment.details;

import android.widget.TextView;

import com.github.yeriomin.yalpstore.YalpStoreActivity;
import com.github.yeriomin.yalpstore.model.App;

public abstract class Abstract {

    protected YalpStoreActivity activity;
    protected App app;

    abstract public void draw();

    public Abstract(YalpStoreActivity activity, App app) {
        this.activity = activity;
        this.app = app;
    }

    protected void setText(int viewId, String text) {
        ((TextView) activity.findViewById(viewId)).setText(text);
    }

    protected void setText(int viewId, int stringId, Object... text) {
        setText(viewId, activity.getString(stringId, text));
    }
}
