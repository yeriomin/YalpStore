package com.dragons.aurora.activities;

import android.os.Bundle;

import com.dragons.aurora.R;
import com.dragons.aurora.fragment.AboutFragment;
import com.dragons.aurora.view.AdaptiveToolbar;

public class AboutActivity extends AuroraActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.helper_activity);

        AdaptiveToolbar dadtb = findViewById(R.id.d_adtb);
        dadtb.getAction_icon().setOnClickListener((v -> this.onBackPressed()));

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, new AboutFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
