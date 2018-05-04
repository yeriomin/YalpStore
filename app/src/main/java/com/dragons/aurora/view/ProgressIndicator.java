package com.dragons.aurora.view;

import android.view.View;

import com.dragons.aurora.R;

public class ProgressIndicator extends ListItem {

    @Override
    public void draw() {
        view.findViewById(R.id.list_container).setVisibility(View.GONE);
        view.findViewById(R.id.progress).setVisibility(View.VISIBLE);
    }
}
