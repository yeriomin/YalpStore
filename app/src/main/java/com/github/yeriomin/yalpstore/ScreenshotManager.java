package com.github.yeriomin.yalpstore;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Gallery;

import com.github.yeriomin.yalpstore.model.App;

public class ScreenshotManager extends DetailsManager {

    public ScreenshotManager(DetailsActivity activity, App app) {
        super(activity, app);
    }

    @Override
    public void draw() {
        if (app.getScreenshotUrls().size() > 0) {
            activity.findViewById(R.id.screenshots_header).setVisibility(View.VISIBLE);
            drawGallery();
            activity.initExpandableGroup(R.id.screenshots_header, R.id.screenshots_container);
        } else {
            activity.findViewById(R.id.screenshots_header).setVisibility(View.GONE);
        }
    }

    private void drawGallery() {
        Gallery gallery = ((Gallery) activity.findViewById(R.id.screenshots_gallery));
        int screenWidth = activity.getWindowManager().getDefaultDisplay().getWidth();
        gallery.setAdapter(new ImageAdapter(activity, app.getScreenshotUrls(), screenWidth));
        gallery.setSpacing(10);
        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(activity, FullscreenImageActivity.class);
                intent.putExtra(FullscreenImageActivity.INTENT_URL, app.getScreenshotUrls().get(position));
                activity.startActivity(intent);
            }
        });
    }
}
