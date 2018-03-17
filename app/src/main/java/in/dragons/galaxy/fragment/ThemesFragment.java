package in.dragons.galaxy.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.aesthetic.Aesthetic;
import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.percolate.caffeine.ViewUtils;

import in.dragons.galaxy.R;

public class ThemesFragment extends UtilFragment {

    private View v;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (v != null) {
            if ((ViewGroup) v.getParent() != null)
                ((ViewGroup) v.getParent()).removeView(v);
            return v;
        }

        v = inflater.inflate(R.layout.app_theme_inc, container, false);
        getActivity().setTitle(R.string.action_themes);
        setupSelection();
        setupTheme();
        setupFab();

        return v;
    }

    private ColorChooserDialog chooserColor(Boolean accentMode) {
        return new ColorChooserDialog.Builder(this.getActivity(), R.string.themes)
                .titleSub(R.string.action_themes)
                .accentMode(accentMode)
                .doneButton(R.string.md_done_label)
                .cancelButton(R.string.md_cancel_label)
                .backButton(R.string.md_back_label)
                .dynamicButtonColor(true)
                .show(getActivity());
    }

    private void setupFab() {
        FloatingActionButton fab = ViewUtils.findViewById(this.getActivity(), R.id.fab);
        fab.setVisibility(View.VISIBLE);
        fab.setImageResource(R.drawable.ic_theme);
        fab.setOnClickListener(view -> Aesthetic.get()
                .colorPrimaryRes(R.color.colorPrimary)
                .colorAccentRes(R.color.colorAccent)
                .colorStatusBarAuto()
                .colorNavigationBarAuto()
                .apply());
    }

    private void setupSelection() {
        TextView primarySelect = (TextView) v.findViewById(R.id.selectPrimary);
        TextView primaryAccent = (TextView) v.findViewById(R.id.selectAccent);
        primarySelect.setOnClickListener(v -> chooserColor(false));
        primaryAccent.setOnClickListener(v -> chooserColor(true));
    }

    private void setupTheme() {
        Button themeLight = (Button) v.findViewById(R.id.themeLight);
        Button themeDark = (Button) v.findViewById(R.id.themeDark);
        Button themeBlack = (Button) v.findViewById(R.id.themeBlack);

        themeLight.setOnClickListener(v -> Aesthetic.get()
                .activityTheme(R.style.AppTheme)
                .isDark(false)
                .colorWindowBackgroundRes(R.color.colorBackground)
                .colorCardViewBackgroundRes(R.color.colorBackgroundCard)
                .colorNavigationBarAuto()
                .textColorPrimaryRes(R.color.colorTextPrimary)
                .textColorSecondaryRes(R.color.colorTextSecondary)
                .apply());

        themeDark.setOnClickListener(v -> Aesthetic.get()
                .activityTheme(R.style.AppTheme_Dark)
                .isDark(true)
                .colorWindowBackgroundRes(R.color.colorBackgroundDark)
                .colorCardViewBackgroundRes(R.color.colorBackgroundCardDark)
                .textColorPrimaryRes(R.color.colorTextPrimaryDark)
                .textColorSecondaryRes(R.color.colorTextSecondaryDark)
                .apply());

        themeBlack.setOnClickListener(v -> Aesthetic.get()
                .activityTheme(R.style.AppTheme_Dark)
                .isDark(true)
                .colorWindowBackgroundRes(R.color.colorBackgroundBlack)
                .colorCardViewBackgroundRes(R.color.colorBackgroundCardBlack)
                .textColorPrimaryRes(R.color.colorTextPrimaryDark)
                .textColorSecondaryRes(R.color.colorTextSecondaryDark)
                .apply());
    }
}