package com.github.yeriomin.yalpstore.view;

import android.content.Intent;
import android.view.View;

import com.github.yeriomin.yalpstore.UpdatableAppsActivity;

public class InstalledAppsMainButtonAdapterAbstract extends ButtonAdapter {

    public InstalledAppsMainButtonAdapterAbstract(final View button) {
        super(button);
    }

    public InstalledAppsMainButtonAdapterAbstract init() {
        enable();
        show();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button.getContext().startActivity(new Intent(button.getContext(), UpdatableAppsActivity.class));
            }
        });
        return this;
    }
}
