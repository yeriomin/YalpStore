package in.dragons.galaxy.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import in.dragons.galaxy.R;
import in.dragons.galaxy.adapters.BigScreenshotsAdapter;
import in.dragons.galaxy.adapters.SmallScreenshotsAdapter;
import it.sephiroth.android.library.widget.HListView;

import static in.dragons.galaxy.activities.DetailsActivity.app;

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

        if (null == app) {
            Log.w(getClass().getSimpleName(), "No app stored");
            finish();
            return;
        }
        List<BigScreenshotsAdapter.Holder> BSAdapter = new ArrayList<>();
        RecyclerView gallery = this.findViewById(R.id.gallery);
        BigScreenshotsAdapter adapter = new BigScreenshotsAdapter(BSAdapter, this);
        for (int i = 0; i < app.getScreenshotUrls().size(); i++)
        BSAdapter.add(new BigScreenshotsAdapter.Holder(app.getScreenshotUrls()));
        gallery.setAdapter(adapter);
        gallery.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        gallery.scrollToPosition(intent.getIntExtra(INTENT_SCREENSHOT_NUMBER, 0));
        adapter.notifyDataSetChanged();
    }

}
