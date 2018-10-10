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

import java.util.HashMap;
import java.util.Map;

public class InstallationState {

    enum STATUS {
        PROCESSING,
        SUCCESS,
        FAILURE
    }

    static private final Map<String, STATUS> apps = new HashMap<>();

    static public boolean isInstalling(String packageName) {
        return apps.keySet().contains(packageName) && apps.get(packageName).equals(STATUS.PROCESSING);
    }

    static public boolean isInstalled(String packageName) {
        return apps.keySet().contains(packageName) && apps.get(packageName).equals(STATUS.SUCCESS);
    }

    static public void setInstalling(String packageName) {
        apps.put(packageName, STATUS.PROCESSING);
    }

    static public void setSuccess(String packageName) {
        apps.put(packageName, STATUS.SUCCESS);
    }

    static public void setFailure(String packageName) {
        apps.put(packageName, STATUS.FAILURE);
    }
}
