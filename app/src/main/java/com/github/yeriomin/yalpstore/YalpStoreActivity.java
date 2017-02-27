package com.github.yeriomin.yalpstore;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.EditText;

public abstract class YalpStoreActivity extends Activity {

    static protected boolean logout = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        logout = false;
        if (isTv()) {
            requestWindowFeature(Window.FEATURE_OPTIONS_PANEL);
        }
        ThemeManager.setTheme(this);
        Thread.setDefaultUncaughtExceptionHandler(new YalpStoreUncaughtExceptionHandler(this));
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (logout) {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, PreferenceActivity.class));
                break;
            case R.id.action_logout:
                showLogOutDialog();
                break;
            case R.id.action_search:
                if (!onSearchRequested()) {
                    showFallbackSearchDialog();
                }
                break;
            case R.id.action_updates:
                startActivity(new Intent(this, UpdatableAppsActivity.class));
                break;
            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private AlertDialog showLogOutDialog() {
        return new AlertDialog.Builder(this)
            .setMessage(R.string.dialog_message_logout)
            .setTitle(R.string.dialog_title_logout)
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    new PlayStoreApiWrapper(getApplicationContext()).logout();
                    dialogInterface.dismiss();
                    finishAll();
                }
            })
            .setNegativeButton(android.R.string.cancel, null)
            .show();
    }

    private AlertDialog showFallbackSearchDialog() {
        final EditText textView = new EditText(this);
        return new AlertDialog.Builder(this)
            .setView(textView)
            .setPositiveButton(android.R.string.search_go, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent i = new Intent(getApplicationContext(), SearchResultActivity.class);
                    i.setAction(Intent.ACTION_SEARCH);
                    i.putExtra(SearchManager.QUERY, textView.getText().toString());
                    startActivity(i);
                }
            })
            .setNegativeButton(android.R.string.cancel, null)
            .show();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private boolean isTv() {
        int uiMode = getResources().getConfiguration().uiMode;
        return (uiMode & Configuration.UI_MODE_TYPE_MASK) == Configuration.UI_MODE_TYPE_TELEVISION;
    }

    protected void finishAll() {
        logout = true;
        finish();
    }
}
