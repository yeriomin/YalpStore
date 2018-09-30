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

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.notification.NotificationManagerWrapper;

abstract public class InstallerBackground extends InstallerAbstract {

    private boolean wasInstalled;

    public InstallerBackground(Context context) {
        super(context);
    }

    @Override
    public boolean verify(App app) {
        new NotificationManagerWrapper(context).show(
            app.getPackageName(),
            new Intent(),
            app.getDisplayName(),
            context.getString(R.string.details_installing)
        );
        if (!super.verify(app)) {
            return false;
        }
        if (background && !new PermissionsComparator(context).isSame(app)) {
            Log.i(getClass().getSimpleName(), "New permissions for " + app.getPackageName());
            notifyNewPermissions(app);
            return false;
        }
        wasInstalled = app.isInstalled();
        return true;
    }

    protected void postInstallationResult(App app, boolean success) {
        String resultString = context.getString(
            success
            ? (wasInstalled ? R.string.notification_installation_complete : R.string.details_installed)
            : (wasInstalled ? R.string.notification_installation_failed : R.string.details_install_failure)
        );
        new NotificationManagerWrapper(context).show(
            app.getPackageName(),
            wasInstalled
                ? HistoryActivity.getHistoryIntent(context, app.getPackageName())
                : DetailsActivity.getDetailsIntent(context, app.getPackageName()),
            app.getDisplayName(),
            resultString
        );
        if (!background && YalpStoreApplication.isForeground()) {
            ContextUtil.toastLong(context, resultString);
        }
        app.setInstalled(true);
    }

    private void notifyNewPermissions(App app) {
        notifyAndToast(
            R.string.notification_download_complete_new_permissions,
            R.string.notification_download_complete_new_permissions_toast,
            app,
            true
        );
    }
}
