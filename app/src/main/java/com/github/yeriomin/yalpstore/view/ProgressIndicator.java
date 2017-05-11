package com.github.yeriomin.yalpstore.view;

import android.view.View;

import com.github.yeriomin.yalpstore.R;

public class ProgressIndicator extends ListItem {

    @Override
    public void draw() {
        view.findViewById(R.id.separator).setVisibility(View.GONE);
        view.findViewById(R.id.app).setVisibility(View.GONE);
        view.findViewById(R.id.progress).setVisibility(View.VISIBLE);
    }
}
