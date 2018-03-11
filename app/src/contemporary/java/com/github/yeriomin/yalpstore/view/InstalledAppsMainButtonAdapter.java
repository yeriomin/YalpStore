package com.github.yeriomin.yalpstore.view;

import android.support.design.widget.FloatingActionButton;
import android.view.View;

import com.github.yeriomin.yalpstore.R;

public class InstalledAppsMainButtonAdapter extends InstalledAppsMainButtonAdapterAbstract {

    public InstalledAppsMainButtonAdapter(View button) {
        super(button);
    }

    @Override
    public InstalledAppsMainButtonAdapterAbstract init() {
        ((FloatingActionButton) button).setImageResource(R.drawable.ic_refresh);
        return super.init();
    }
}
