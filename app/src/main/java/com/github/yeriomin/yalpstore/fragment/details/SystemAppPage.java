package com.github.yeriomin.yalpstore.fragment.details;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.DetailsActivity;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.view.IntentOnClickListener;

public class SystemAppPage extends Abstract {

    public SystemAppPage(DetailsActivity activity, App app) {
        super(activity, app);
    }

    @Override
    public void draw() {
        if (!app.isInstalled()) {
            return;
        }
        TextView systemAppInfo = activity.findViewById(R.id.system_app_info);
        systemAppInfo.setVisibility(View.VISIBLE);
        systemAppInfo.setOnClickListener(new IntentOnClickListener(activity) {
            @Override
            protected Intent buildIntent() {
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
        });
    }
}
