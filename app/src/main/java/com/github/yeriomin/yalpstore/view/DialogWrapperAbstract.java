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

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ListAdapter;

public abstract class DialogWrapperAbstract implements DialogInterface {

    protected Activity activity;

    public DialogWrapperAbstract(Activity activity) {
        this.activity = activity;
    }

    abstract public DialogWrapperAbstract setMessage(int stringResId);
    abstract public DialogWrapperAbstract setMessage(CharSequence message);
    abstract public DialogWrapperAbstract setTitle(int stringResId);
    abstract public DialogWrapperAbstract setTitle(CharSequence title);
    abstract public DialogWrapperAbstract setIcon(Drawable icon);
    abstract public DialogWrapperAbstract setPositiveButton(int stringResId, DialogInterface.OnClickListener listener);
    abstract public DialogWrapperAbstract setNegativeButton(int stringResId, DialogInterface.OnClickListener listener);
    abstract public DialogWrapperAbstract setNeutralButton(int stringResId, DialogInterface.OnClickListener listener);
    abstract public DialogWrapperAbstract setOnDismissListener(OnDismissListener listener);
    abstract public DialogWrapperAbstract setView(View view);
    abstract public DialogWrapperAbstract setLayout(int layoutResId);
    abstract public View findViewById(int viewId);
    abstract public DialogWrapperAbstract setItems(int arrayResId, DialogInterface.OnClickListener listener);
    abstract public DialogWrapperAbstract setItems(CharSequence[] items, DialogInterface.OnClickListener listener);
    abstract public DialogWrapperAbstract setAdapter(ListAdapter listAdapter, DialogInterface.OnClickListener listener);
    abstract public DialogWrapperAbstract setCancelable(boolean cancelable);
    abstract public DialogWrapperAbstract create();
    abstract public DialogWrapperAbstract show();
}
