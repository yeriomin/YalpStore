package com.dragons.aurora.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dragons.aurora.LocaleManager;
import com.dragons.aurora.fragment.PreferenceFragment;
import com.percolate.caffeine.PhoneUtils;
import com.percolate.caffeine.ToastUtils;

public abstract class BaseActivity extends AppCompatActivity {

    static protected boolean logout = false;

    public static void cascadeFinish() {
        BaseActivity.logout = true;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
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
}
