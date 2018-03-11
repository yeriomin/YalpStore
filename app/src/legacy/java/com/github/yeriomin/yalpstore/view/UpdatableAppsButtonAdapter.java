package com.github.yeriomin.yalpstore.view;

import android.view.View;
import android.widget.Button;

import com.github.yeriomin.yalpstore.R;

public class UpdatableAppsButtonAdapter extends UpdatableAppsButtonAdapterAbstract {

    public UpdatableAppsButtonAdapter(View button) {
        super(button);
    }

    @Override
    public UpdatableAppsButtonAdapterAbstract setReady() {
        ((Button) button).setText(R.string.list_update_all);
        return super.setReady();
    }

    @Override
    public UpdatableAppsButtonAdapterAbstract setUpdating() {
        ((Button) button).setText(R.string.list_updating);
        return super.setUpdating();
    }
}
