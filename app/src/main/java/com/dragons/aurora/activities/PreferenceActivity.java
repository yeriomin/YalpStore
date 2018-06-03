package com.dragons.aurora.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dragons.aurora.R;
import com.dragons.aurora.fragment.PreferenceFragment;
import com.dragons.aurora.view.AdaptiveToolbar;


public class PreferenceActivity extends AuroraActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preference_ativity);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(getResources().getColor(R.color.semi_transparent));
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, new PreferenceFragment())
                .commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, AuroraActivity.class));
        this.finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}