package in.dragons.galaxy.fragment.details;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import in.dragons.galaxy.DetailsActivity;
import in.dragons.galaxy.R;
import in.dragons.galaxy.model.App;

public class SystemAppPage extends Abstract {

    public SystemAppPage(DetailsActivity activity, App app) {
        super(activity, app);
    }

    @Override
    public void draw() {
        if (!app.isInstalled()) {
            return;
        }
        ImageView systemAppInfo = (ImageView) activity.findViewById(R.id.system_app_info);
        systemAppInfo.setVisibility(View.VISIBLE);
        systemAppInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity();
            }
        });
    }

    private void startActivity() {
        try {
            activity.startActivity(getIntent());
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
