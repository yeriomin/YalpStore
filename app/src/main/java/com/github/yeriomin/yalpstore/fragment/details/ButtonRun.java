package com.github.yeriomin.yalpstore.fragment.details;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.view.View;

import com.github.yeriomin.yalpstore.DetailsActivity;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.YalpStoreApplication;
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
    protected void onButtonClick(View v) {
        Intent i = getLaunchIntent();
        if (null != i) {
            activity.startActivity(i);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private Intent getLaunchIntent() {
        Intent i = activity.getPackageManager().getLaunchIntentForPackage(app.getPackageName());
        boolean isTv = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && ((YalpStoreApplication) activity.getApplication()).isTv();
        if (isTv) {
            Intent l = activity.getPackageManager().getLeanbackLaunchIntentForPackage(app.getPackageName());
            if (null != l) {
                i = l;
            }
        }
        if (i == null) {
            return null;
        }
        i.addCategory(isTv ? Intent.CATEGORY_LEANBACK_LAUNCHER : Intent.CATEGORY_LAUNCHER);
        return i;
    }
}
