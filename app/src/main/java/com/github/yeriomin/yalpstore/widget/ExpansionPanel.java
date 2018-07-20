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

package com.github.yeriomin.yalpstore.widget;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.Util;

import java.util.ArrayList;
import java.util.List;

public class ExpansionPanel extends LinearLayout {

    private static final int ANIMATION_DURATION = 100;

    private CharSequence headerText;
    private boolean isCreated = false;

    private TextView headerView;
    private LinearLayout containerView;
    private OnClickListener onClickListener;

    public void setHeaderText(int resourceId, String... args) {
        setHeaderText(getContext().getString(resourceId, (Object[]) args));
    }

    public void setHeaderText(CharSequence headerText) {
        this.headerText = headerText;
        if (null != headerView) {
            headerView.setText(headerText);
        }
    }

    public void toggle() {
        if (null != headerView) {
            headerView.performClick();
        }
    }

    public ExpansionPanel(Context context) {
        this(context, null);
        init();
    }

    public ExpansionPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ExpansionPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ExpansionPanel(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        init();
    }

    @Override
    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    private void init() {
        setOrientation(VERTICAL);
        setClipToPadding(false);
        if (!isCreated && getChildCount() > 0) {
            isCreated = true;
            // It is just a linear layout with children now
            // So we save all the children and remove them
            List<View> children = new ArrayList<>();
            for (int i = 0; i < getChildCount(); i++) {
                children.add(getChildAt(i));
            }
            removeAllViews();
            // Now it becomes an empty expansion panel
            inflate(getContext(), R.layout.expansion_panel_widget_layout, this);
            // Putting the detached children into the container
            containerView = findViewById(R.id.container);
            for (View child: children) {
                containerView.addView(child);
            }
            headerView = findViewById(R.id.header);
            setHeaderText(headerText);
            headerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isExpanded = containerView.getHeight() > 0;
                    if (isExpanded) {
                        collapse(containerView);
                        headerView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand_more, 0);
                    } else {
                        if (null != onClickListener) {
                            onClickListener.onClick(v);
                        }
                        expand(containerView);
                        headerView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand_less, 0);
                    }
                }
            });
        }
    }

    private void init(AttributeSet attrs) {
        init();
        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(
            attrs,
            R.styleable.ExpansionPanel,
            0,
            0
        );
        try {
            headerText = typedArray.getString(R.styleable.ExpansionPanel_headerText);
        } finally {
            typedArray.recycle();
        }
    }

    private void expand(View v) {
        containerView.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        int targetHeight = containerView.getMeasuredHeight() + Util.getPx(v.getContext(), 16);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            animate(v, ANIMATION_DURATION, targetHeight);
        } else {
            v.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
    }

    private void collapse(View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            animate(v, ANIMATION_DURATION, 0);
        } else {
            v.getLayoutParams().height = 0;
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void animate(final View v, int duration, final int targetHeight) {
        v.setVisibility(View.VISIBLE);
        ValueAnimator valueAnimator = ValueAnimator.ofInt(v.getHeight(), targetHeight);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.getLayoutParams().height = (int) animation.getAnimatedValue();
                v.requestLayout();
                if (v.getLayoutParams().height == targetHeight && targetHeight > 0) {
                    v.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                }
            }
        });
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }
}
