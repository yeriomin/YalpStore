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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ListAdapter;

import com.github.yeriomin.yalpstore.ContextUtil;

public class DialogWrapper extends DialogWrapperAbstract {

    protected AlertDialog.Builder builder;
    protected AlertDialog dialog;
    protected View view;

    private OnDismissListener onDismissListener;

    public DialogWrapper(Activity activity) {
        super(activity);
        builder = new AlertDialog.Builder(activity);
    }

    @Override
    public DialogWrapperAbstract setMessage(int stringResId) {
        builder.setMessage(stringResId);
        return this;
    }

    @Override
    public DialogWrapperAbstract setMessage(CharSequence message) {
        builder.setMessage(message);
        return this;
    }

    @Override
    public DialogWrapperAbstract setTitle(int stringResId) {
        builder.setTitle(stringResId);
        return this;
    }

    @Override
    public DialogWrapperAbstract setTitle(CharSequence title) {
        builder.setTitle(title);
        return this;
    }

    @Override
    public DialogWrapperAbstract setIcon(Drawable icon) {
        builder.setIcon(icon);
        return this;
    }

    @Override
    public DialogWrapperAbstract setPositiveButton(int stringResId, OnClickListener listener) {
        builder.setPositiveButton(stringResId, listener);
        return this;
    }

    @Override
    public DialogWrapperAbstract setNegativeButton(int stringResId, OnClickListener listener) {
        builder.setNegativeButton(stringResId, listener);
        return this;
    }

    @Override
    public DialogWrapperAbstract setNeutralButton(int stringResId, OnClickListener listener) {
        builder.setNeutralButton(stringResId, listener);
        return this;
    }

    @Override
    public DialogWrapperAbstract setOnDismissListener(OnDismissListener listener) {
        onDismissListener = listener;
        return this;
    }

    @Override
    public DialogWrapperAbstract setView(View view) {
        this.view = view;
        builder.setView(view);
        return this;
    }

    @Override
    public DialogWrapperAbstract setLayout(int layoutResId) {
        setView(activity.getLayoutInflater().inflate(layoutResId, null));
        return this;
    }

    @Override
    public View findViewById(int viewId) {
        if (null == view) {
            return null;
        }
        return view.findViewById(viewId);
    }

    @Override
    public DialogWrapperAbstract setItems(int arrayResId, DialogInterface.OnClickListener listener) {
        builder.setItems(arrayResId, listener);
        return this;
    }

    @Override
    public DialogWrapperAbstract setItems(CharSequence[] items, DialogInterface.OnClickListener listener) {
        builder.setItems(items, listener);
        return this;
    }

    @Override
    public DialogWrapperAbstract setAdapter(ListAdapter listAdapter, OnClickListener listener) {
        builder.setAdapter(listAdapter, listener);
        return this;
    }

    @Override
    public DialogWrapperAbstract setCancelable(boolean cancelable) {
        builder.setCancelable(cancelable);
        return this;
    }

    @Override
    public DialogWrapperAbstract create() {
        ContextUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog = builder.create();
                if (null != onDismissListener) {
                    dialog.setOnDismissListener(onDismissListener);
                }
            }
        });
        return this;
    }

    @Override
    public DialogWrapperAbstract show() {
        if (null == dialog) {
            create();
        }
        ContextUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.show();
            }
        });
        return this;
    }

    @Override
    public void cancel() {
        if (null != dialog) {
            ContextUtil.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.cancel();
                }
            });
        }
    }

    @Override
    public void dismiss() {
        if (null != dialog) {
            ContextUtil.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                }
            });
        }
    }
}
