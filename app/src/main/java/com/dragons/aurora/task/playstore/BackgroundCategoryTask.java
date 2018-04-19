package com.dragons.aurora.task.playstore;

import com.dragons.aurora.activities.AuroraActivity;

class BackgroundCategoryTask extends CategoryTask {

    protected void fill() {
        if (context instanceof AuroraActivity) {
            ((AuroraActivity) context).invalidateOptionsMenu();
        }
    }
}
