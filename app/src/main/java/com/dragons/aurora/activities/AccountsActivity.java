package com.dragons.aurora.activities;

import android.os.Bundle;
import android.view.View;

import com.dragons.aurora.R;
import com.dragons.aurora.fragment.AccountsFragment;

public class AccountsActivity extends AuroraActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.helper_activity_alt);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, new AccountsFragment())
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

    public void userChanged() {
        this.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, new AccountsFragment())
                .commit();
    }
}
