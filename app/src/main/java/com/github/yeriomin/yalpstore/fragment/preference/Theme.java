package com.github.yeriomin.yalpstore.fragment.preference;

import android.preference.ListPreference;
import android.preference.Preference;

import com.github.yeriomin.yalpstore.PreferenceActivity;
import com.github.yeriomin.yalpstore.PreferenceUtil;
import com.github.yeriomin.yalpstore.R;

public class Theme extends Abstract {

    private ListPreference themePreference;

    public Theme(PreferenceActivity activity) {
        super(activity);
    }

    public void setThemePreference(ListPreference themePreference) {
        this.themePreference = themePreference;
    }

    @Override
    public void draw() {
        Preference.OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, final Object newValue) {
                preference.setSummary(activity.getString(getThemeSummaryStringId((String) newValue)));
                return true;
            }
        };
        listener.onPreferenceChange(themePreference, themePreference.getValue());
        themePreference.setOnPreferenceChangeListener(listener);
    }

    private int getThemeSummaryStringId(String theme) {
        if (null == theme) {
            return R.string.pref_ui_theme_none;
        }
        int summaryId;
        switch (theme) {
            case PreferenceUtil.THEME_LIGHT:
                summaryId = R.string.pref_ui_theme_light;
                break;
            case PreferenceUtil.THEME_DARK:
                summaryId = R.string.pref_ui_theme_dark;
                break;
            case PreferenceUtil.THEME_BLACK:
                summaryId = R.string.pref_ui_theme_black;
                break;
            case PreferenceUtil.THEME_NONE:
            default:
                summaryId = R.string.pref_ui_theme_none;
                break;
        }
        return summaryId;
    }
}
