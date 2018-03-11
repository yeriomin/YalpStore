package com.github.yeriomin.yalpstore.view;

import android.view.View;
import android.widget.Button;

import com.github.yeriomin.yalpstore.R;

public class InstalledAppsMainButtonAdapter extends InstalledAppsMainButtonAdapterAbstract {

    public InstalledAppsMainButtonAdapter(View button) {
        super(button);
    }

    @Override
    public InstalledAppsMainButtonAdapterAbstract init() {
        ((Button) button).setText(R.string.list_check_updates);
        return super.init();
    }
}
