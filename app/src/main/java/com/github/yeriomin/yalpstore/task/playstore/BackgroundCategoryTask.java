package com.github.yeriomin.yalpstore.task.playstore;

import android.os.Build;

import com.github.yeriomin.yalpstore.YalpStoreActivity;

class BackgroundCategoryTask extends CategoryTask {

    @Override
    protected void fill() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && context instanceof YalpStoreActivity) {
            ((YalpStoreActivity) context).invalidateOptionsMenu();
        }
    }
}
