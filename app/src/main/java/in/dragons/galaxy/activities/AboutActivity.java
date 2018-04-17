package in.dragons.galaxy.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.percolate.caffeine.ViewUtils;

import in.dragons.galaxy.R;
import in.dragons.galaxy.fragment.AboutFragment;
import in.dragons.galaxy.view.AdaptiveToolbar;

public class AboutActivity extends GalaxyActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.helper_activity);

        AdaptiveToolbar dadtb = findViewById(R.id.d_adtb);
        dadtb.getAvatar_icon().setOnClickListener((v -> this.onBackPressed()));

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
