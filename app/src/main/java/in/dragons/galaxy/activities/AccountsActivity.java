package in.dragons.galaxy.activities;

import android.os.Bundle;
import android.view.View;

import in.dragons.galaxy.R;
import in.dragons.galaxy.fragment.AccountsFragment;

public class AccountsActivity extends GalaxyActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.accounts_activity);

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
