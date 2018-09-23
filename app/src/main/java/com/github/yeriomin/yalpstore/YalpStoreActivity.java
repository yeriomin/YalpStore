/*
 * Yalp Store
 * Copyright (C) 2018 Sergey Yeriomin <yeriomin@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.github.yeriomin.yalpstore;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.EditText;

import com.github.yeriomin.yalpstore.fragment.FilterMenu;
import com.github.yeriomin.yalpstore.model.LoginInfo;
import com.github.yeriomin.yalpstore.model.LoginInfoDao;
import com.github.yeriomin.yalpstore.view.DialogWrapper;
import com.github.yeriomin.yalpstore.view.DialogWrapperAbstract;
import com.github.yeriomin.yalpstore.view.LoginDialogBuilder;

import java.util.Collections;
import java.util.List;

import info.guardianproject.netcipher.proxy.OrbotHelper;

import static com.github.yeriomin.yalpstore.PlayStoreApiAuthenticator.PREFERENCE_USER_ID;
import static com.github.yeriomin.yalpstore.PreferenceUtil.PREFERENCE_USE_TOR;

public abstract class YalpStoreActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(getClass().getSimpleName(), "Starting activity");
        if (((YalpStoreApplication) getApplication()).isTv()) {
            requestWindowFeature(Window.FEATURE_OPTIONS_PANEL);
        }
        new ThemeManager().setTheme(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        Log.v(getClass().getSimpleName(), "Resuming activity");
        YalpStoreApplication.incrementActivityCount();
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            invalidateOptionsMenu();
        }
        if (PreferenceUtil.getBoolean(this, PREFERENCE_USE_TOR)) {
            OrbotHelper.requestStartTor(this);
        }
    }

    @Override
    protected void onPause() {
        Log.v(getClass().getSimpleName(), "Pausing activity");
        YalpStoreApplication.decrementActivityCount();
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.v(getClass().getSimpleName(), "Stopping activity");
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bar, menu);
        new FilterMenu(this).onCreateOptionsMenu(menu);
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
            case R.id.action_installed_apps:
                startActivity(new Intent(this, InstalledAppsActivity.class));
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
            case R.id.action_wishlist:
                startActivity(new Intent(this, WishlistActivity.class));
                break;
            case R.id.action_history:
                startActivity(new Intent(this, HistoryActivity.class));
                break;
            case R.id.filter_system_apps:
            case R.id.filter_apps_with_ads:
            case R.id.filter_paid_apps:
            case R.id.filter_gsf_dependent_apps:
            case R.id.filter_category:
            case R.id.filter_rating:
            case R.id.filter_downloads:
                new FilterMenu(this).onOptionsItemSelected(item);
                break;
            case R.id.action_new_account:
                new LoginDialogBuilder(this).show();
                return true;
            case R.id.action_accounts:
                return true;
            default:
                if (item.getGroupId() != R.id.group_accounts) {
                    return super.onOptionsItemSelected(item);
                }
                return switchAccount(item.getItemId());
        }
        return true;
    }

    @Override
    protected void fillAccountList(Menu menu, List<LoginInfo> users) {
        if (null == users || users.isEmpty()) {
            return;
        }
        Menu accountsMenu = null == menu.findItem(R.id.action_accounts).getSubMenu() ? menu : menu.findItem(R.id.action_accounts).getSubMenu();
        for (LoginInfo info: users) {
            String userName = info.getUserName();
            String deviceDefinitionDisplayName = "";
            if (!TextUtils.isEmpty(info.getDeviceDefinitionName())) {
                deviceDefinitionDisplayName = getString(R.string.bullet_divider) + info.getDeviceDefinitionDisplayName();
                userName = userName.split(" ")[0];
            }
            MenuItem item = accountsMenu.add(
                R.id.group_accounts,
                info.hashCode(),
                1,
                userName + deviceDefinitionDisplayName
            );
            if (info.hashCode() == YalpStoreApplication.user.hashCode()) {
                markCurrentAccount(item);
            }
        }
    }

    @Override
    protected List<LoginInfo> getUsers() {
        SQLiteDatabase db = new SqliteHelper(this).getReadableDatabase();
        List<LoginInfo> users = new LoginInfoDao(db).getAll();
        db.close();
        Collections.sort(users);
        return users;
    }

    private boolean switchAccount(int id) {
        if (id == YalpStoreApplication.user.hashCode()) {
            return false;
        }
        SQLiteDatabase db = new SqliteHelper(this).getReadableDatabase();
        LoginInfo loginInfo = new LoginInfoDao(db).get(id);
        db.close();
        if (null == loginInfo) {
            return false;
        }
        new PlayStoreApiAuthenticator(this).logout();
        YalpStoreApplication.user = loginInfo;
        PreferenceUtil.getDefaultSharedPreferences(this).edit().putInt(PREFERENCE_USER_ID, YalpStoreApplication.user.hashCode()).commit();
        redrawAccounts();
        return true;
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

    protected DialogWrapperAbstract showLogOutDialog() {
        return new DialogWrapper(this)
            .setMessage(R.string.dialog_message_logout)
            .setTitle(R.string.dialog_title_logout)
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    new PlayStoreApiAuthenticator(getApplicationContext()).logout();
                    redrawAccounts();
                    dialogInterface.dismiss();
                }
            })
            .setNeutralButton(R.string.delete, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    new PlayStoreApiAuthenticator(getApplicationContext()).logout(true);
                    redrawAccounts();
                    dialogInterface.dismiss();
                }
            })
            .setNegativeButton(android.R.string.cancel, null)
            .show()
        ;
    }

    private DialogWrapperAbstract showFallbackSearchDialog() {
        final EditText textView = new EditText(this);
        return new DialogWrapper(this)
            .setView(textView)
            .setPositiveButton(android.R.string.search_go, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent i = new Intent(getApplicationContext(), SearchActivity.class);
                    i.setAction(Intent.ACTION_SEARCH);
                    i.putExtra(SearchManager.QUERY, textView.getText().toString());
                    startActivity(i);
                }
            })
            .setNegativeButton(android.R.string.cancel, null)
            .show()
        ;
    }
}
