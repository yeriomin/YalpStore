package com.dragons.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dragons.aurora.R;

public class CustomAppBar extends NestedScrollView {

    public static final int DISABLE_BLUR = 0;
    public static final int ON_EXPANDED = 50;
    public static final int FULL_BLUR = 100;

    public static final String MORE_ICON_TAG = "more_icon_tag";

    private int blurRadius = 20;
    private int backgroundAlpha = 78;

    private BottomSheetBehavior bottomSheetBehavior;

    private int backgroundColour;
    private int foregroundColour;
    private int customAppBarType;
    private boolean keepRipple;

    private MenuSecondaryItemsAdapter menuSecondaryItemsAdapter;
    private MenuNavigationItemsAdapter menuNavigationItemsAdapter;

    public CustomAppBar(Context context) {
        super(context);
        init();
    }

    public CustomAppBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomAppBar, 0, 0);
        try {
            backgroundColour = a.getColor(R.styleable.CustomAppBar_background_colour, Color.WHITE);
            foregroundColour = a.getColor(R.styleable.CustomAppBar_foreground_colour, Color.DKGRAY);
            customAppBarType = a.getInt(R.styleable.CustomAppBar_app_bar_type, FULL_BLUR);
        } finally {
            a.recycle();
        }

        init();
    }

    private void init() {
        setClipToPadding(true);
        setBackgroundColor(backgroundColour);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setElevation(getResources().getDimension(R.dimen.appbar_bar_elevation));
        }

        LayoutInflater.from(getContext()).inflate(R.layout.content_app_bar, this, true);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        bottomSheetBehavior = BottomSheetBehavior.from(this);
        bottomSheetBehavior.setPeekHeight((int) getResources().getDimension(R.dimen.appbar_bar_height));
        bottomSheetBehavior.setHideable(false);
        bottomSheetBehavior.setBottomSheetCallback(bottomSheetCallback);

        View moreIcon = findViewWithTag(MORE_ICON_TAG);
        moreIcon.setOnClickListener(onMoreClickListener);

        if (customAppBarType == FULL_BLUR) {
            handleShowBlur();
        }

        if (customAppBarType == DISABLE_BLUR) {
            keepRipple = false;
        }
    }

    ///// ********************************************** /////
    ///// *********    PUBLIC APIS/METHODS    ********** /////
    ///// ********************************************** /////

    public void setNavigationMenu(@MenuRes int menuRes, OnClickListener onClickListener) {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.nav_items_recycler);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        menuNavigationItemsAdapter = new MenuNavigationItemsAdapter(getContext(), menuRes, onClickListener,
                foregroundColour);
        recyclerView.setAdapter(menuNavigationItemsAdapter);
    }

    public void setSecondaryMenu(@MenuRes int menuRes, OnClickListener onClickListener) {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.secondary_menu_items_recyler);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        menuSecondaryItemsAdapter = new MenuSecondaryItemsAdapter(getContext(), menuRes, onClickListener,
                foregroundColour);
        recyclerView.setAdapter(menuSecondaryItemsAdapter);
    }

    public int getBackgroundColour() {
        return backgroundColour;
    }

    public void setBackgroundColour(@ColorInt int backgroundColour) {
        this.backgroundColour = backgroundColour;
        this.setBackgroundColor(backgroundColour);
    }

    public int getForegroundColour() {
        return foregroundColour;
    }

    public void setForegroundColour(@ColorInt int foregroundColour) {
        this.foregroundColour = foregroundColour;

        menuNavigationItemsAdapter.setForegroundColour(foregroundColour);
        menuNavigationItemsAdapter.notifyDataSetChanged();

        menuSecondaryItemsAdapter.setForegroundColour(foregroundColour);
        menuSecondaryItemsAdapter.notifyDataSetChanged();
    }

    public int getcustomAppBarType() {
        return customAppBarType;
    }

    public void setcustomAppBarType(int customAppBarType) {
        this.customAppBarType = customAppBarType;
        menuNavigationItemsAdapter.setForegroundColour(foregroundColour);
        menuNavigationItemsAdapter.notifyDataSetChanged();

        menuSecondaryItemsAdapter.setForegroundColour(foregroundColour);
        menuSecondaryItemsAdapter.notifyDataSetChanged();
    }

    public boolean isRipple() {
        return keepRipple;
    }

    public void setRipple(boolean keepRipple) {
        this.keepRipple = keepRipple;
        menuNavigationItemsAdapter.setKeepRipple(keepRipple);
        menuSecondaryItemsAdapter.setKeepRipple(keepRipple);
    }

    public void setBlurRadius(int blurRadius) {
        this.blurRadius = blurRadius;
    }

    public int getBlurRadius() {
        return blurRadius;
    }

    public int getBackgroundAlpha() {
        return backgroundAlpha;
    }

    public void setBackgroundAlpha(int backgroundAlpha) {
        this.backgroundAlpha = backgroundAlpha;
    }

    public void collapse() {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }, 500);
    }

    public void collapseWithoutDelay() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    public void expand() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    ///// ********************************************** /////
    ///// *********    END OF APIS/METHODS    ********** /////
    ///// ********************************************** /////


    private OnClickListener onMoreClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }
    };

    private BottomSheetBehavior.BottomSheetCallback bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (customAppBarType == ON_EXPANDED) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    handleShowBlur();
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    BlurView blurView = (BlurView) findViewById(R.id.blurview);
                    blurView.setBlurEnabled(false);
                    setBackgroundColor(backgroundColour);
                }
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    private void handleShowBlur() {
        setBackground(null);
        final ViewGroup rootView = (ViewGroup) getRootView();
        final Drawable windowBackground = getBackground();
        BlurView blurView = (BlurView) findViewById(R.id.blurview);
        blurView.setupWith(rootView)
                .windowBackground(windowBackground)
                .blurAlgorithm(new RenderScriptBlur(getContext()))
                .blurRadius(blurRadius);
        int transparentBackgroundColour = Color.argb(backgroundAlpha,
                Color.red(backgroundColour),
                Color.green(backgroundColour),
                Color.blue(backgroundColour));
        blurView.setOverlayColor(transparentBackgroundColour);
    }

}

