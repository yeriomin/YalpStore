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

package com.github.yeriomin.yalpstore.fragment;

import android.view.View;

import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.YalpStoreActivity;
import com.github.yeriomin.yalpstore.YalpStoreApplication;
import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.view.PurchaseDialogBuilder;
import com.github.yeriomin.yalpstore.view.UriOnClickListener;

public class ButtonBuy extends Button {

    public ButtonBuy(YalpStoreActivity activity, App app) {
        super(activity, app);
    }

    @Override
    protected View getButton() {
        return activity.findViewById(R.id.buy);
    }

    @Override
    protected boolean shouldBeVisible() {
        return !YalpStoreApplication.purchasedPackageNames.contains(app.getPackageName()) && !app.isInstalled() && !app.isFree();
    }

    @Override
    public void draw() {
        super.draw();
        android.widget.Button button = (android.widget.Button) getButton();
        if (null != button) {
            button.setText(app.getPrice());
        }
    }

    @Override
    protected void onButtonClick(View v) {
        new UriOnClickListener(activity, PurchaseDialogBuilder.URL_PURCHASE + app.getPackageName()).onClick(v);
    }
}
