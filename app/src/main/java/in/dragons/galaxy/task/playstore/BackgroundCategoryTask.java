package in.dragons.galaxy.task.playstore;

import in.dragons.galaxy.activities.GalaxyActivity;

class BackgroundCategoryTask extends CategoryTask {

    protected void fill() {
        if (context instanceof GalaxyActivity) {
            ((GalaxyActivity) context).invalidateOptionsMenu();
        }
    }
}
