package com.github.yeriomin.yalpstore.fragment.details;

import android.content.pm.PackageManager;
import android.view.View;

import com.github.yeriomin.yalpstore.DetailsActivity;
import com.github.yeriomin.yalpstore.model.App;

public abstract class Button extends Abstract {

    protected View button;

    public Button(DetailsActivity activity, App app) {
        super(activity, app);
        this.button = getButton();
    }

    abstract protected View getButton();

    abstract protected boolean shouldBeVisible();

    abstract protected View.OnClickListener getOnClickListener();

    @Override
    public void draw() {
        if (null == button) {
            return;
        }
        button.setEnabled(true);
        button.setVisibility(shouldBeVisible() ? View.VISIBLE : View.GONE);
        button.setOnClickListener(getOnClickListener());
    }

    protected void disableButton(int buttonId, int stringId) {
        android.widget.Button button = (android.widget.Button) activity.findViewById(buttonId);
        button.setText(stringId);
        button.setEnabled(false);
    }

    protected boolean isInstalled() {
        try {
            activity.getPackageManager().getPackageInfo(app.getPackageName(), 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
