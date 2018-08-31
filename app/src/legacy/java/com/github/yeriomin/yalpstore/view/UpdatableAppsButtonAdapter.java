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

package com.github.yeriomin.yalpstore.view;

import android.view.View;
import android.widget.Button;

import com.github.yeriomin.yalpstore.R;

public class UpdatableAppsButtonAdapter extends UpdatableAppsButtonAdapterAbstract {

    public UpdatableAppsButtonAdapter(View button) {
        super(button);
    }

    @Override
    public UpdatableAppsButtonAdapterAbstract setReady() {
        ((Button) button).setText(R.string.list_update_all);
        return super.setReady();
    }

    @Override
    public UpdatableAppsButtonAdapterAbstract setUpdating() {
        ((Button) button).setText(android.R.string.cancel);
        return super.setUpdating();
    }
}
