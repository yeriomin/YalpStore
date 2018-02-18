package com.github.yeriomin.yalpstore.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.R;

public class Badge extends LinearLayout {

    public Badge(Context context) {
        super(context);
        init();
    }

    public Badge(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Badge(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Badge(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    public ImageView getIconView() {
        return findViewById(R.id.icon);
    }

    public void setIcon(Drawable drawable) {
        ((ImageView) findViewById(R.id.icon)).setImageDrawable(drawable);
    }

    public void setLabel(CharSequence label) {
        ((TextView) findViewById(R.id.label)).setText(label);
    }

    private void init() {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        inflate(getContext(), R.layout.badge_widget_layout, this);
    }

    private void init(AttributeSet attrs) {
        init();
        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(
            attrs,
            R.styleable.Badge,
            0,
            0
        );
        try {
            ((ImageView) findViewById(R.id.icon)).setImageResource(typedArray.getResourceId(R.styleable.Badge_icon, 0));
            setLabel(typedArray.getString(R.styleable.Badge_label));
        } finally {
            typedArray.recycle();
        }
    }
}
