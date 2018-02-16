package in.dragons.galaxy.fragment.details;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import in.dragons.galaxy.DetailsActivity;
import in.dragons.galaxy.R;
import in.dragons.galaxy.model.App;
import in.dragons.galaxy.task.playstore.PurchaseTask;

public class BackToPlayStore extends Abstract {

    static private final String PLAY_STORE_PACKAGE_NAME = "com.android.vending";

    public BackToPlayStore(DetailsActivity activity, App app) {
        super(activity, app);
    }

    @Override
    public void draw() {
        if (!isPlayStoreInstalled() || !app.isInPlayStore()) {
            return;
        }
        ImageView toPlayStore = (ImageView) activity.findViewById(R.id.to_play_store);
        toPlayStore.setVisibility(View.VISIBLE);
        toPlayStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(PurchaseTask.URL_PURCHASE + app.getPackageName()));
                activity.startActivity(i);
            }
        });
    }

    private boolean isPlayStoreInstalled() {
        try {
            return null != activity.getPackageManager().getPackageInfo(PLAY_STORE_PACKAGE_NAME, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
