package in.dragons.galaxy.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;

import com.afollestad.aesthetic.Aesthetic;
import com.afollestad.aesthetic.AestheticActivity;
import com.afollestad.aesthetic.NavigationViewMode;
import com.afollestad.aesthetic.TabLayoutBgMode;
import com.afollestad.aesthetic.TabLayoutIndicatorMode;
import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.percolate.caffeine.ViewUtils;

import in.dragons.galaxy.R;
import in.dragons.galaxy.fragment.ThemesFragment;

public class ThemesActivity extends AestheticActivity implements ColorChooserDialog.ColorCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.helper_activity);
        Toolbar toolbar = ViewUtils.findViewById(this, R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, new ThemesFragment())
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

    @Override
    public void onColorSelection(@NonNull ColorChooserDialog dialog, int selectedColor) {
        if (dialog.isAccentMode()) {
            Aesthetic.get()
                    .colorAccent(selectedColor)
                    .apply();
        } else {
            Aesthetic.get()
                    .colorPrimary(selectedColor)
                    .colorStatusBarAuto()
                    .colorNavigationBarAuto()
                    .navigationViewMode(NavigationViewMode.SELECTED_ACCENT)
                    .tabLayoutBackgroundMode(TabLayoutBgMode.PRIMARY)
                    .tabLayoutIndicatorMode(TabLayoutIndicatorMode.ACCENT)
                    .apply();
        }
    }

    @Override
    public void onColorChooserDismissed(@NonNull ColorChooserDialog dialog) {
    }
}
