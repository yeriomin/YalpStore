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
    abstract public DialogWrapperAbstract setView(View view);
    abstract public DialogWrapperAbstract setLayout(int layoutResId);
    abstract public View findViewById(int viewId);
    abstract public DialogWrapperAbstract setItems(int arrayResId, DialogInterface.OnClickListener listener);
    abstract public DialogWrapperAbstract setAdapter(ListAdapter listAdapter, DialogInterface.OnClickListener listener);
    abstract public DialogWrapperAbstract setCancelable(boolean cancelable);
    abstract public DialogWrapperAbstract create();
    abstract public DialogWrapperAbstract show();
}
