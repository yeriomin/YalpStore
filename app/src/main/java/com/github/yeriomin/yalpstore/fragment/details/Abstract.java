package com.github.yeriomin.yalpstore.fragment.details;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.R;
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

    protected void initExpandableGroup(int viewIdHeader, int viewIdContainer, final View.OnClickListener l) {
        TextView viewHeader = (TextView) activity.findViewById(viewIdHeader);
        viewHeader.setVisibility(View.VISIBLE);
        final LinearLayout viewContainer = (LinearLayout) activity.findViewById(viewIdContainer);
        viewHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isExpanded = viewContainer.getVisibility() == View.VISIBLE;
                if (isExpanded) {
                    viewContainer.setVisibility(View.GONE);
                    ((TextView) v).setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.ic_expand_more,0);
                } else {
                    if (null != l) {
                        l.onClick(v);
                    }
                    viewContainer.setVisibility(View.VISIBLE);
                    ((TextView) v).setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.ic_expand_less,0);
                }
            }
        });
    }

    protected void initExpandableGroup(int viewIdHeader, int viewIdContainer) {
        initExpandableGroup(viewIdHeader, viewIdContainer, null);
    }
}
