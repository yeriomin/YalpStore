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

package com.github.yeriomin.yalpstore.model;

import android.content.pm.ApplicationInfo;

public class ImageSource {

    private String url;
    private ApplicationInfo applicationInfo;
    private boolean fullSize;

    public ImageSource() {
    }

    public ImageSource(String url) {
        setUrl(url);
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setApplicationInfo(ApplicationInfo applicationInfo) {
        this.applicationInfo = applicationInfo;
    }

    public String getUrl() {
        return url;
    }

    public ApplicationInfo getApplicationInfo() {
        return applicationInfo;
    }

    public boolean isFullSize() {
        return fullSize;
    }

    public ImageSource setFullSize(boolean fullSize) {
        this.fullSize = fullSize;
        return this;
    }
}
