package com.github.yeriomin.yalpstore.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.R;

import java.util.ArrayList;
import java.util.List;

public class ExpansionPanel extends LinearLayout {

    private CharSequence headerText;
    private boolean isCreated = false;

    private TextView headerView;
    private LinearLayout containerView;
    private OnClickListener onClickListener;

    public void setHeaderText(int resourceId, String... args) {
        setHeaderText(getContext().getString(resourceId, args));
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
                    boolean isExpanded = containerView.getVisibility() == View.VISIBLE;
                    if (isExpanded) {
                        containerView.setVisibility(View.GONE);
                        headerView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand_more, 0);
                    } else {
                        if (null != onClickListener) {
                            onClickListener.onClick(v);
                        }
                        containerView.setVisibility(View.VISIBLE);
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
}
