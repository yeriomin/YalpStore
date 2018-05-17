package com.dragons.custom;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.Gravity;


public class AppBarTextView extends AppCompatTextView {

    public AppBarTextView(Context context) {
        super(context);
        init();
    }

    public AppBarTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AppBarTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setLines(1);
        setGravity(Gravity.CENTER);
    }
}
