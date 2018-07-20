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

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageInstaller;
import android.os.Build;
import android.support.v4.content.FileProvider;

import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.task.InstalledAppsTask;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class InstallerPrivilegedSession extends InstallerPrivileged {

    private static final String BROADCAST_ACTION_INSTALL = BuildConfig.APPLICATION_ID + ".ACTION_INSTALL_COMMIT";
    private static final String BROADCAST_SENDER_PERMISSION = "android.permission.INSTALL_PACKAGES";
    private static final String EXTRA_LEGACY_STATUS = "android.content.pm.extra.LEGACY_STATUS";

    private final InstallationResultReceiver broadcastReceiver = new InstallationResultReceiver();

    public InstallerPrivilegedSession(Context context) {
        super(context);
    }

    @Override
    protected void install(App app) {
        super.install(app);
        registerReceiver();
        PackageInstaller packageInstaller = context.getPackageManager().getPackageInstaller();
        PackageInstaller.Session session = null;
        try {
            int sessionId = packageInstaller.createSession(new PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL));
            byte[] buffer = new byte[65536];
            session = packageInstaller.openSession(sessionId);
            InputStream in = context.getContentResolver().openInputStream(FileProvider.getUriForFile(
                context,
                BuildConfig.APPLICATION_ID + ".fileprovider",
                Paths.getApkPath(context, app.getPackageName(), app.getVersionCode())
            ));
            OutputStream out = session.openWrite(app.getPackageName(), 0, -1);
            try {
                int c;
                while ((c = in.read(buffer)) != -1) {
                    out.write(buffer, 0, c);
                }
                session.fsync(out);
            } finally {
                com.github.yeriomin.yalpstore.Util.closeSilently(in);
                com.github.yeriomin.yalpstore.Util.closeSilently(out);
            }
            session.commit(getIntentSender(sessionId));
        } catch (IOException e) {
            fail(e, app.getPackageName());
        } finally {
            com.github.yeriomin.yalpstore.Util.closeSilently(session);
        }
    }

    @Override
    protected void processResult(App app, int returnCode) {
        super.processResult(app, returnCode);
        context.unregisterReceiver(broadcastReceiver);
    }

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BROADCAST_ACTION_INSTALL);
        context.registerReceiver(broadcastReceiver, intentFilter, BROADCAST_SENDER_PERMISSION, null);
    }

    private IntentSender getIntentSender(int sessionId) {
        return PendingIntent.getBroadcast(
            context,
            sessionId,
            new Intent(BROADCAST_ACTION_INSTALL),
            PendingIntent.FLAG_UPDATE_CURRENT
        ).getIntentSender();
    }

    @Override
    protected void finalize() throws Throwable {
        context.unregisterReceiver(broadcastReceiver);
        super.finalize();
    }

    private class InstallationResultReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            processResult(
                InstalledAppsTask.getInstalledApp(context.getPackageManager(), intent.getStringExtra(PackageInstaller.EXTRA_PACKAGE_NAME)),
                intent.getIntExtra(EXTRA_LEGACY_STATUS, PackageInstaller.STATUS_FAILURE)
            );
        }
    }
}
