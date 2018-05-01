package com.dragons.aurora.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.percolate.caffeine.ViewUtils;

import com.dragons.aurora.R;
import com.dragons.aurora.fragment.SpoofFragment;

public class SpoofActivity extends AuroraActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.helper_activity);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, new SpoofFragment())
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
