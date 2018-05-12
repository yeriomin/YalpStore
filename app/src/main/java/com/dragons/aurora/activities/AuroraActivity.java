package com.dragons.aurora.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.dragons.aurora.R;
import com.dragons.aurora.adapters.ViewPagerAdapter;
import com.dragons.aurora.model.App;
import com.dragons.custom.CustomAppBar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AuroraActivity extends BaseActivity implements View.OnClickListener {

    static public App app;
    static int static_pos = -9;

    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.bottom_bar)
    CustomAppBar bottm_bar;

    public static void setPosition(int item) {
        static_pos = item;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        bottm_bar.setNavigationMenu(R.menu.main_menu, this);
        bottm_bar.setSecondaryMenu(R.menu.nav_menu, this);
        bottm_bar.setBlurRadius(10);
        viewPager.setAdapter(new ViewPagerAdapter(this, getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(3);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        if (logout) {
            finish();
        }
        if (static_pos != -9) {
            viewPager.setCurrentItem(static_pos, true);
            static_pos = -9;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void unregisterReceiver(BroadcastReceiver receiver) {
        if (null == receiver) {
            return;
        }
        try {
            super.unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
            // Ignoring
        }
    }

    @Override
    public void onClick(View v) {
        switch ((int) v.getTag()) {
            case R.id.action_home:
                viewPager.setCurrentItem(0, true);
                break;
            case R.id.action_myapps:
                viewPager.setCurrentItem(1, true);
                break;
            case R.id.action_updates:
                viewPager.setCurrentItem(2, true);
                break;
            case R.id.action_categories:
                viewPager.setCurrentItem(3, true);
                break;
            case R.id.action_search:
                viewPager.setCurrentItem(4, true);
                break;
            case R.id.action_accounts:
                startActivity(new Intent(getApplicationContext(), AccountsActivity.class));
                break;
            case R.id.action_about:
                startActivity(new Intent(getApplicationContext(), AboutActivity.class));
                break;
            case R.id.action_settings:
                startActivity(new Intent(getApplicationContext(), PreferenceActivity.class));
                break;
        }
    }
}
