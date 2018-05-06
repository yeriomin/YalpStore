package com.dragons.aurora.fragment.details;

import android.widget.TextView;

import com.dragons.aurora.activities.AuroraActivity;
import com.dragons.aurora.model.App;

public abstract class Abstract {

    protected AuroraActivity activity;
    protected App app;

    public Abstract(AuroraActivity activity, App app) {
        this.activity = activity;
        this.app = app;
    }

    abstract public void draw();

    protected void setText(int viewId, String text) {
        TextView textView = (TextView) activity.findViewById(viewId);
        if (null != textView)
            textView.setText(text);
    }

    protected void setText(int viewId, int stringId, Object... text) {
        setText(viewId, activity.getString(stringId, text));
    }
}
