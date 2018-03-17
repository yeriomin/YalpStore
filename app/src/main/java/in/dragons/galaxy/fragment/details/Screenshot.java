package in.dragons.galaxy.fragment.details;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Gallery;

import in.dragons.galaxy.activities.DetailsActivity;
import in.dragons.galaxy.activities.FullscreenImageActivity;
import in.dragons.galaxy.adapters.ImageAdapter;
import in.dragons.galaxy.R;
import in.dragons.galaxy.model.App;

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
        Gallery gallery = ((Gallery) activity.findViewById(R.id.screenshots_gallery));
        int screenWidth = activity.getWindowManager().getDefaultDisplay().getWidth();
        gallery.setAdapter(new ImageAdapter(activity, app.getScreenshotUrls(), screenWidth));
        gallery.setSpacing(15);
        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(activity, FullscreenImageActivity.class);
                intent.putExtra(FullscreenImageActivity.INTENT_SCREENSHOT_NUMBER, position);
                activity.startActivity(intent);
            }
        });
    }
}