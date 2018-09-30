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

import com.github.yeriomin.playstoreapi.GooglePlayAPI;

public class RequestDelta extends Request {

    private byte[] baseHash;
    private GooglePlayAPI.PATCH_FORMAT patchFormat;

    public byte[] getBaseHash() {
        return baseHash;
    }

    public void setBaseHash(byte[] baseHash) {
        this.baseHash = baseHash;
    }

    public GooglePlayAPI.PATCH_FORMAT getPatchFormat() {
        return patchFormat;
    }

    public void setPatchFormat(GooglePlayAPI.PATCH_FORMAT patchFormat) {
        this.patchFormat = patchFormat;
    }

    @Override
    public Type getType() {
        return Type.DELTA;
    }
}
