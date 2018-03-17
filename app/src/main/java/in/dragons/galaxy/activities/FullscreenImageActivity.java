package in.dragons.galaxy.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Gallery;

import in.dragons.galaxy.R;
import in.dragons.galaxy.adapters.FullscreenImageAdapter;

public class FullscreenImageActivity extends Activity {

    static public final String INTENT_SCREENSHOT_NUMBER = "INTENT_SCREENSHOT_NUMBER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.fullscreen_image_activity_layout);

        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (null == DetailsActivity.app) {
            Log.w(getClass().getSimpleName(), "No app stored");
            finish();
            return;
        }

        Gallery gallery = ((Gallery) findViewById(R.id.gallery));
        gallery.setAdapter(new FullscreenImageAdapter(
                this,
                DetailsActivity.app.getScreenshotUrls(),
                getWindowManager().getDefaultDisplay().getWidth(),
                getWindowManager().getDefaultDisplay().getHeight()
        ));
        gallery.setSelection(intent.getIntExtra(INTENT_SCREENSHOT_NUMBER, 0));
    }

}
