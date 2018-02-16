package in.dragons.galaxy.fragment.details;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.View;

import in.dragons.galaxy.DetailsActivity;
import in.dragons.galaxy.GalaxyApplication;
import in.dragons.galaxy.R;
import in.dragons.galaxy.model.App;

public class ButtonRun extends Button {

    public ButtonRun(DetailsActivity activity, App app) {
        super(activity, app);
    }

    @Override
    protected android.widget.Button getButton() {
        if (activity.findViewById(R.id.download).getVisibility() == View.VISIBLE)
            return null;
        else
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
            try {
                activity.startActivity(i);
            } catch (ActivityNotFoundException e) {
                Log.e(getClass().getName(), "getLaunchIntentForPackage returned an intent, but starting the activity failed for " + app.getPackageName());
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private Intent getLaunchIntent() {
        Intent i = activity.getPackageManager().getLaunchIntentForPackage(app.getPackageName());
        boolean isTv = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && ((GalaxyApplication) activity.getApplication()).isTv();
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
