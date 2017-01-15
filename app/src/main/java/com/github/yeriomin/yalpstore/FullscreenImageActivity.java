package com.github.yeriomin.yalpstore;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class FullscreenImageActivity extends Activity {

    static public final String INTENT_URL = "INTENT_URL";

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

        String url = intent.getStringExtra(INTENT_URL);
        BitmapManager manager = new BitmapManager(this);
        Bitmap bitmap = manager.getBitmap(url, true);
        ((ImageView) findViewById(R.id.image)).setImageBitmap(bitmap);
    }
}
