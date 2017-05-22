package com.github.yeriomin.yalpstore.fragment.details;

import android.content.Intent;
import android.view.View;

import com.github.yeriomin.yalpstore.DetailsActivity;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.model.App;

public class ButtonRun extends Button {

    public ButtonRun(DetailsActivity activity, App app) {
        super(activity, app);
    }

    @Override
    protected android.widget.Button getButton() {
        return (android.widget.Button) activity.findViewById(R.id.run);
    }

    @Override
    protected boolean shouldBeVisible() {
        return isInstalled() && null != getLaunchIntent();
    }

    @Override
    protected View.OnClickListener getOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(getLaunchIntent());
            }
        };
    }

    private Intent getLaunchIntent() {
        Intent i = activity.getPackageManager().getLaunchIntentForPackage(app.getPackageName());
        if (i == null) {
            return null;
        }
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        return i;
    }
}
