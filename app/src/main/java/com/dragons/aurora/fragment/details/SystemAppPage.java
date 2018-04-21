package com.dragons.aurora.fragment.details;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.dragons.aurora.activities.DetailsActivity;
import com.dragons.aurora.R;
import com.dragons.aurora.fragment.DetailsFragment;
import com.dragons.aurora.model.App;

public class SystemAppPage extends AbstractHelper {

    public SystemAppPage(DetailsFragment fragment, App app) {
        super(fragment, app);
    }

    @Override
    public void draw() {
        if (!app.isInstalled()) {
            return;
        }
        ImageView systemAppInfo = (ImageView) fragment.getActivity().findViewById(R.id.system_app_info);
        systemAppInfo.setVisibility(View.VISIBLE);
        systemAppInfo.setOnClickListener(v -> startActivity());
    }

    private void startActivity() {
        try {
            fragment.getActivity().startActivity(getIntent());
        } catch (ActivityNotFoundException e) {
            Log.w(getClass().getSimpleName(), "Could not find system app activity");
        }
    }

    private Intent getIntent() {
        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", Uri.parse("package:" + app.getPackageName()));
        } else {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            intent.putExtra("com.android.settings.ApplicationPkgName", app.getPackageName());
            intent.putExtra("pkg", app.getPackageName());
        }
        return intent;
    }
}