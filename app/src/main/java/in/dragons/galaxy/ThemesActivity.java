package in.dragons.galaxy;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.aesthetic.Aesthetic;
import com.afollestad.aesthetic.NavigationViewMode;
import com.afollestad.materialdialogs.color.ColorChooserDialog;

public class ThemesActivity extends GalaxyActivity implements ColorChooserDialog.ColorCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.app_theme_inc, contentFrameLayout);

        setupSelection();
        setupTheme();
        setupFab();
    }

    private ColorChooserDialog chooserPrimary() {
        return new ColorChooserDialog.Builder(this, R.string.themes)
                .titleSub(R.string.action_themes)
                .accentMode(false)
                .doneButton(R.string.md_done_label)
                .cancelButton(R.string.md_cancel_label)
                .backButton(R.string.md_back_label)
                .dynamicButtonColor(true)
                .show(this); //
    }

    private ColorChooserDialog chooserAccent() {
        return new ColorChooserDialog.Builder(this, R.string.themes)
                .titleSub(R.string.action_themes)
                .accentMode(true)
                .doneButton(R.string.md_done_label)
                .cancelButton(R.string.md_cancel_label)
                .backButton(R.string.md_back_label)
                .dynamicButtonColor(true)
                .show(this);
    }

    private void setupFab() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        fab.setImageResource(R.drawable.ic_theme);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Aesthetic.get()
                        .colorPrimaryRes(R.color.colorPrimary)
                        .colorAccentRes(R.color.colorAccent)
                        .colorStatusBarAuto()
                        .colorNavigationBarAuto()
                        .apply();
            }
        });
    }

    private void setupSelection() {
        TextView primarySelect = (TextView) findViewById(R.id.selectPrimary);
        TextView primaryAccent = (TextView) findViewById(R.id.selectAccent);

        primarySelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooserPrimary();
            }
        });

        primaryAccent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooserAccent();
            }
        });
    }

    private void setupTheme() {
        Button themeLight = (Button) findViewById(R.id.themeLight);
        Button themeDark = (Button) findViewById(R.id.themeDark);
        Button themeBlack = (Button) findViewById(R.id.themeBlack);

        themeLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Aesthetic.get()
                        .activityTheme(R.style.AppTheme)
                        .isDark(false)
                        .colorWindowBackgroundRes(R.color.colorBackground)
                        .colorCardViewBackgroundRes(R.color.colorBackgroundCard)
                        .colorNavigationBarAuto()
                        .textColorPrimaryRes(R.color.colorTextPrimary)
                        .textColorSecondaryRes(R.color.colorTextSecondary)
                        .apply();
            }
        });

        themeDark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Aesthetic.get()
                        .activityTheme(R.style.AppTheme_Dark)
                        .isDark(true)
                        .colorWindowBackgroundRes(R.color.colorBackgroundDark)
                        .colorCardViewBackgroundRes(R.color.colorBackgroundCardDark)
                        .textColorPrimaryRes(R.color.colorTextPrimaryDark)
                        .textColorSecondaryRes(R.color.colorTextSecondaryDark)
                        .apply();
            }
        });

        themeBlack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Aesthetic.get()
                        .activityTheme(R.style.AppTheme_Dark)
                        .isDark(true)
                        .colorWindowBackgroundRes(R.color.colorBackgroundBlack)
                        .colorCardViewBackgroundRes(R.color.colorBackgroundCardBlack)
                        .textColorPrimaryRes(R.color.colorTextPrimaryDark)
                        .textColorSecondaryRes(R.color.colorTextSecondaryDark)
                        .apply();
            }
        });
    }

    @Override
    public void onColorSelection(@NonNull ColorChooserDialog dialog, int selectedColor) {
        if (dialog.isAccentMode()) {
            Aesthetic.get().colorAccent(selectedColor);
            ImageView accentView = (ImageView) findViewById(R.id.accentView);
            accentView.setBackgroundColor(selectedColor);
        } else {
            Aesthetic.get()
                    .colorPrimary(selectedColor)
                    .colorStatusBarAuto()
                    .colorNavigationBarAuto()
                    .navigationViewMode(NavigationViewMode.SELECTED_ACCENT)
                    .apply();
            ImageView primaryView = (ImageView) findViewById(R.id.primaryView);
            primaryView.setBackgroundColor(selectedColor);
        }
    }

    @Override
    public void onColorChooserDismissed(@NonNull ColorChooserDialog dialog) {

    }
}
