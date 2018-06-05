package com.dragons.aurora.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dragons.aurora.R;
import com.dragons.aurora.fragment.PreferenceFragment;
import com.percolate.caffeine.PhoneUtils;
import com.percolate.caffeine.ToastUtils;

public abstract class BaseActivity extends AppCompatActivity {

    static protected boolean logout = false;

    public static void cascadeFinish() {
        BaseActivity.logout = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        logout = false;
    }

    protected boolean isConnected() {
        return PhoneUtils.isNetworkAvailable(this);
    }

    protected boolean isLoggedIn() {
        return PreferenceFragment.getBoolean(this, "LOGGED_IN");
    }

    protected boolean isDummy() {
        return PreferenceFragment.getBoolean(this, "DUMMY_ACC");
    }

    protected boolean isGoogle() {
        return PreferenceFragment.getBoolean(this, "GOOGLE_ACC");
    }

    protected void notifyConnected(final Context context) {
        if (!isConnected())
            ToastUtils.quickToast(this, "No network").show();
    }

    protected int getThemeFromPref() {
        String Theme = PreferenceFragment.getString(this, "PREFERENCE_THEME");
        switch (Theme) {
            case "Light":
                return R.style.AppTheme;
            case "Dark":
                return R.style.AppTheme_Dark;
            case "Black":
                return R.style.AppTheme_Black;
            default:
                return R.style.AppTheme;
        }
    }
}