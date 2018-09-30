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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class PackageSpecificReceiver extends BroadcastReceiver {

    protected String packageName;

    @Override
    public void onReceive(Context context, Intent intent) {
        packageName = intent.getStringExtra(Intent.EXTRA_PACKAGE_NAME);
        List<String> extras = new ArrayList<>();
        if (null != intent.getExtras() && !intent.getExtras().isEmpty()) {
            for (String key: intent.getExtras().keySet()) {
                extras.add(key + "=" + intent.getExtras().get(key));
            }
        }
        Log.i(getClass().getSimpleName(), "Caught " + intent.getAction() + " intent: data " + intent.getData() + " extras {" + TextUtils.join(", ", extras) + "}");
    }
}
