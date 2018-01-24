package in.dragons.galaxy.task.playstore;

import android.os.Build;

import in.dragons.galaxy.YalpStoreActivity;

class BackgroundCategoryTask extends CategoryTask {

    @Override
    protected void fill() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && context instanceof YalpStoreActivity) {
            ((YalpStoreActivity) context).invalidateOptionsMenu();
        }
    }
}
