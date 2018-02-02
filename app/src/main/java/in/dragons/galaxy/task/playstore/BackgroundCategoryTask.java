package in.dragons.galaxy.task.playstore;

import android.os.Build;

import in.dragons.galaxy.GalaxyActivity;

class BackgroundCategoryTask extends CategoryTask {

    @Override
    protected void fill() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && context instanceof GalaxyActivity) {
            ((GalaxyActivity) context).invalidateOptionsMenu();
        }
    }
}
