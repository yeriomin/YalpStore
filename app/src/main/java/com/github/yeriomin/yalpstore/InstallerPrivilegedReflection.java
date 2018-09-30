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
import android.content.pm.IPackageInstallObserver;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.RemoteException;

import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.task.InstalledAppsTask;

import java.lang.reflect.InvocationTargetException;

class InstallerPrivilegedReflection extends InstallerPrivileged {

    @Override
    protected void install(App app) {
        PackageManager pm = context.getPackageManager();
        Class<?>[] types = new Class[] {Uri.class, IPackageInstallObserver.class, int.class, String.class};
        try {
            pm.getClass().getMethod("installPackage", types).invoke(
                pm,
                Uri.fromFile(Paths.getApkPath(context, app.getPackageName(), app.getVersionCode())),
                new InstallObserver(),
                INSTALL_REPLACE_EXISTING,
                BuildConfig.APPLICATION_ID
            );
        } catch (NoSuchMethodException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            fail(e, app.getPackageName());
        }
    }

    public InstallerPrivilegedReflection(Context context) {
        super(context);
    }

    class InstallObserver extends IPackageInstallObserver.Stub {

        @Override
        public void packageInstalled(String packageName, int returnCode) throws RemoteException {
            processResult(InstalledAppsTask.getInstalledApp(context.getPackageManager(), packageName), returnCode);
        }
    }
}
