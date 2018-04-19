package com.dragons.aurora.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.percolate.caffeine.ViewUtils;

import com.dragons.aurora.R;
import com.dragons.aurora.fragment.PreferenceFragment;
import com.dragons.aurora.view.AdaptiveToolbar;


public class PreferenceActivity extends AuroraActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.helper_activity);
        AdaptiveToolbar dadtb = findViewById(R.id.d_adtb);
        if (dadtb.getAction_icon().getContentDescription().toString().equals("details")) {
            dadtb.getAction_icon().setOnClickListener(v -> {
                this.onBackPressed();
            });
        }
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, new PreferenceFragment())
                .commitAllowingStateLoss();
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