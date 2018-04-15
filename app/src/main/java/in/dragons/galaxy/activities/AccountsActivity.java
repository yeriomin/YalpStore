package in.dragons.galaxy.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.percolate.caffeine.ViewUtils;

import in.dragons.galaxy.R;
import in.dragons.galaxy.fragment.AccountsFragment;
import in.dragons.galaxy.view.AdaptiveToolbar;

public class AccountsActivity extends GalaxyActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.helper_activity);
        AdaptiveToolbar dadtb = findViewById(R.id.d_adtb);
        if (dadtb.getAction_icon().getContentDescription().toString().equals("details")) {
            dadtb.getAction_icon().setOnClickListener(v -> {
                finish();
            });
        }
        dadtb.getTitle0().setText("Accounts");
        dadtb.getTitle1().setVisibility(View.GONE);
        dadtb.getDownload_section().setVisibility(View.GONE);
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
