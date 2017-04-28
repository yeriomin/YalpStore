package com.github.yeriomin.yalpstore;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.github.yeriomin.yalpstore.model.App;

public class DetailsButtonRunFragment extends DetailsButtonFragment {

    public DetailsButtonRunFragment(DetailsActivity activity, App app) {
        super(activity, app);
    }

    @Override
    protected Button getButton() {
        return (Button) activity.findViewById(R.id.run);
    }

    @Override
    protected boolean shouldBeVisible() {
        return app.isInstalled() && null != getLaunchIntent();
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
