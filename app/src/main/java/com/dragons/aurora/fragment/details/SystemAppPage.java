package com.dragons.aurora.fragment.details;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.dragons.aurora.R;
import com.dragons.aurora.fragment.DetailsFragment;
import com.dragons.aurora.model.App;

public class SystemAppPage extends AbstractHelper {

    public SystemAppPage(DetailsFragment fragment, App app) {
        super(fragment, app);
    }

    @Override
    public void draw() {
        ImageView systemAppInfo = (ImageView) fragment.getActivity().findViewById(R.id.system_app_info);

        if (!app.isInstalled()) {
            hide(fragment.getView(), R.id.system_app_info);
            return;
        }
        show(fragment.getView(), R.id.system_app_info);
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
        return new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", Uri.parse("package:" + app.getPackageName()));
    }
}