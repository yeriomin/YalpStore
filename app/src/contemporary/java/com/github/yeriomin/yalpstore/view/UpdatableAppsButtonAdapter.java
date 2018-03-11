package com.github.yeriomin.yalpstore.view;

import android.support.design.widget.FloatingActionButton;
import android.view.View;

import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.UpdatableAppsActivity;

public class UpdatableAppsButtonAdapter extends UpdatableAppsButtonAdapterAbstract {

    public UpdatableAppsButtonAdapter(View button) {
        super(button);
    }

    @Override
    public UpdatableAppsButtonAdapterAbstract init(UpdatableAppsActivity activity) {
        ((FloatingActionButton) button).setImageResource(R.drawable.ic_download);
        return super.init(activity);
    }
}
