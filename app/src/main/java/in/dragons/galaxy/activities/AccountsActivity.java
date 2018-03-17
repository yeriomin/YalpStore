package in.dragons.galaxy.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.percolate.caffeine.ViewUtils;

import in.dragons.galaxy.R;
import in.dragons.galaxy.fragment.AccountsFragment;

public class AccountsActivity extends GalaxyActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.helper_activity);
        Toolbar toolbar = ViewUtils.findViewById(this, R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, new AccountsFragment())
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
