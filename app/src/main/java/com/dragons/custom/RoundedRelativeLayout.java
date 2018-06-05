package com.dragons.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.Dimension;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.RelativeLayout;

import com.dragons.aurora.R;
import com.dragons.aurora.ViewUtils;


public class RoundedRelativeLayout extends RelativeLayout {

    @Dimension
    private float customRadius;
    private float customShadow;
    @ColorInt
    private int customBackgroundColor;
    private int customShadowColor;

    public RoundedRelativeLayout(Context context) {
        super(context);
        initBackground();
    }

    public RoundedRelativeLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RoundedRelativeLayout, 0, 0);

        try {
            customRadius = a.getDimensionPixelSize(R.styleable.RoundedRelativeLayout_customRRadius, 0);
            customShadow = a.getDimensionPixelSize(R.styleable.RoundedRelativeLayout_customRShadow, 0);
            customShadowColor = a.getColor(R.styleable.RoundedRelativeLayout_customRShadowColor, Color.GRAY);
            customBackgroundColor = a.getColor(R.styleable.RoundedRelativeLayout_customRBackgroundColor, Color.WHITE);
        } finally {
            a.recycle();
        }
        initBackground();
    }

    public RoundedRelativeLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initBackground();
    }

    public float getCustomRadius() {
        return customRadius;
    }

    public void setCustomRadius(float customRadius) {
        this.customRadius = customRadius;
    }

    public float getCustomShadow() {
        return customShadow;
    }

    public void setCustomShadow(float customShadow) {
        this.customShadow = customShadow;
    }

    public int getCustomBackground() {
        return customBackgroundColor;
    }

    public void setCustomBackground(int customBackground) {
        this.customBackgroundColor = customBackground;
    }

    public int getCustomShadowColor() {
        return customShadowColor;
    }

    public void setCustomShadowColor(int customShadowColor) {
        this.customShadowColor = customShadowColor;
    }

    public void apply() {
        initBackground();
    }

    private void initBackground() {
        setBackground(ViewUtils.generateBackgroundWithShadow(this,
                customBackgroundColor,
                customRadius,
                customShadowColor,
                customShadow,
                Gravity.CENTER)
        );
    }
}