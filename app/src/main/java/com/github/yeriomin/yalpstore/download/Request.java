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

package com.github.yeriomin.yalpstore.download;

/**
 * Stores info required to download an app
 * Mostly contains info from AndroidAppDeliveryData message
 *
 */
public abstract class Request {

    /**
     * At most three files are downloaded for one app
     * Apk (which is replaced by a delta if allowed and available)
     * and optional primary and secondary obb files
     *
     */
    public enum Type {
        APK,
        SPLIT,
        DELTA,
        OBB_MAIN,
        OBB_PATCH
    }

    private String packageName;
    private String url;
    private String cookieString;
    private long size;
    private byte[] hash;
    private boolean gzipped;
    private java.io.File destination;

    public abstract Type getType();

    public String getTypeName() {
        return getType().name();
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCookieString() {
        return cookieString;
    }

    public void setCookieString(String cookieString) {
        this.cookieString = cookieString;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public byte[] getHash() {
        return hash;
    }

    public void setHash(byte[] hash) {
        this.hash = hash;
    }

    public boolean isGzipped() {
        return gzipped;
    }

    public void setGzipped(boolean gzipped) {
        this.gzipped = gzipped;
    }

    public java.io.File getDestination() {
        return destination;
    }

    public void setDestination(java.io.File destination) {
        this.destination = destination;
    }
}
