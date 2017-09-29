package com.github.yeriomin.yalpstore;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.EditText;

public abstract class YalpStoreActivity extends Activity {

    private static final int PERMISSIONS_REQUEST_CODE = 384;
    private static final String TAG = YalpStoreActivity.class.getSimpleName();

    static protected boolean logout = false;

    public static void cascadeFinish() {
        YalpStoreActivity.logout = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(getClass().getName(), "Starting activity");
        logout = false;
        if (((YalpStoreApplication) getApplication()).isTv()) {
            requestWindowFeature(Window.FEATURE_OPTIONS_PANEL);
        }
        ThemeManager.setTheme(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        Log.v(getClass().getName(), "Resuming activity");
        super.onResume();
        if (logout) {
            finish();
        }
    }

    @Override
    protected void onPause() {
        Log.v(getClass().getName(), "Pausing activity");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.v(getClass().getName(), "Stopping activity");
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem updates = menu.findItem(R.id.action_updates);
        if (null != updates && !PreferenceActivity.getBoolean(this, PreferenceActivity.PREFERENCE_UPDATES_ONLY)) {
            updates.setTitle(R.string.activity_title_updates_and_other_apps);
        }
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
            case R.id.action_categories:
                startActivity(new Intent(this, CategoryListActivity.class));
                break;
            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
            case R.id.action_bug_report:
                startActivity(new Intent(this, BugReportActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected boolean isGranted(int requestCode, String permissions[], int[] grantResults) {
        return requestCode == PERMISSIONS_REQUEST_CODE
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ;
    }

    public boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    public void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_CODE
            );
        }
    }

    private AlertDialog showLogOutDialog() {
        return new AlertDialog.Builder(this)
                .setMessage(R.string.dialog_message_logout)
                .setTitle(R.string.dialog_title_logout)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new PlayStoreApiAuthenticator(getApplicationContext()).logout();
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
                        Intent i = new Intent(getApplicationContext(), SearchActivity.class);
                        i.setAction(Intent.ACTION_SEARCH);
                        String query = textView.getText().toString();
                        i.putExtra(SearchManager.QUERY, query);
                        LogHelper.d(TAG, "showFallbackSearchDialog-->onClick--> query:" + query);
                        startActivity(i);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    protected void finishAll() {
        logout = true;
        finish();
    }
}
