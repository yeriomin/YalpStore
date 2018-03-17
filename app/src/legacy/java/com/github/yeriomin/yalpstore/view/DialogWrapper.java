package com.github.yeriomin.yalpstore.view;

import android.app.AlertDialog;
import android.os.Build;
import android.view.View;

import com.github.yeriomin.yalpstore.YalpStoreActivity;

public class DialogWrapper extends DialogWrapperAbstract {

    AlertDialog.Builder builder;
    AlertDialog dialog;

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
    public DialogWrapperAbstract setView(View view) {
        builder.setView(view);
        return this;
    }

    @Override
    public DialogWrapperAbstract setLayout(int layoutResId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setView(layoutResId);
        } else {
            builder.setView(activity.getLayoutInflater().inflate(layoutResId, null));
        }
        return this;
    }

    @Override
    public View findViewById(int viewId) {
        if (null == dialog) {
            create();
        }
        return dialog.findViewById(viewId);
    }

    @Override
    public DialogWrapperAbstract setItems(int arrayResId, DialogInterface.OnClickListener listener) {
        builder.setItems(arrayResId, listener);
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
        dialog = builder.create();
        return this;
    }

    @Override
    public DialogWrapperAbstract show() {
        if (null == dialog) {
            create();
        }
        dialog.show();
        return this;
    }

    @Override
    public void cancel() {
        if (null != dialog) {
            dialog.cancel();
        }
    }

    @Override
    public void dismiss() {
        if (null != dialog) {
            dialog.dismiss();
        }
    }
}
