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

package com.github.yeriomin.yalpstore.install;

import android.content.Context;
import android.os.Build;

import com.github.yeriomin.yalpstore.PreferenceUtil;
import com.github.yeriomin.yalpstore.YalpStorePermissionManager;

public class InstallerFactory {

    static public InstallerAbstract get(Context context) {
        switch (PreferenceUtil.getString(context, PreferenceUtil.PREFERENCE_INSTALLATION_METHOD)) {
            case PreferenceUtil.INSTALLATION_METHOD_PRIVILEGED:
                return getPrivilegedInstaller(context);
            case PreferenceUtil.INSTALLATION_METHOD_ROOT:
                return new InstallerRoot(context);
            case PreferenceUtil.INSTALLATION_METHOD_DEFAULT:
                return new InstallerDefault(context);
            default:
                return YalpStorePermissionManager.hasInstallPermission(context)
                    ? getPrivilegedInstaller(context)
                    : new InstallerDefault(context)
                ;
        }
    }

    static private InstallerPrivileged getPrivilegedInstaller(Context context) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
            ? new InstallerPrivilegedSession(context)
            : new InstallerPrivilegedReflection(context)
        ;
    }
}
