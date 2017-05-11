package com.github.yeriomin.yalpstore.view;

import android.view.View;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.R;

public class ListSeparator extends ListItem {

    private String label;

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public void draw() {
        view.findViewById(R.id.separator).setVisibility(View.VISIBLE);
        view.findViewById(R.id.app).setVisibility(View.GONE);
        view.findViewById(R.id.progress).setVisibility(View.GONE);
        ((TextView) view.findViewById(R.id.label)).setText(label);
    }
}
