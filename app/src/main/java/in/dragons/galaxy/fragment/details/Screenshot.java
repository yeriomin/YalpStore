package in.dragons.galaxy.fragment.details;

import android.content.Intent;

import in.dragons.galaxy.R;
import in.dragons.galaxy.activities.DetailsActivity;
import in.dragons.galaxy.activities.FullscreenImageActivity;
import in.dragons.galaxy.adapters.ImageAdapter;
import in.dragons.galaxy.model.App;
import it.sephiroth.android.library.widget.HListView;

public class Screenshot extends Abstract {

    public Screenshot(DetailsActivity activity, App app) {
        super(activity, app);
    }

    @Override
    public void draw() {
        if (app.getScreenshotUrls().size() > 0) {
            drawGallery();
        } else {
            return;
        }
    }

    private void drawGallery() {
        HListView gallery = activity.findViewById(R.id.screenshots_gallery);
        int screenWidth = activity.getWindowManager().getDefaultDisplay().getWidth();
        gallery.setAdapter(new ImageAdapter(activity, app.getScreenshotUrls(), screenWidth));
        gallery.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(activity, FullscreenImageActivity.class);
            intent.putExtra(FullscreenImageActivity.INTENT_SCREENSHOT_NUMBER, position);
            activity.startActivity(intent);
        });
    }
}