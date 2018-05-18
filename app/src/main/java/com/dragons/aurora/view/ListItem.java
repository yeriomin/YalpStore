package com.dragons.aurora.view;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public abstract class ListItem {

    protected View view;

    public void setView(View view) {
        this.view = view;
    }

    abstract public void draw();

    void hide(View view) {
        if (view != null)
            view.setVisibility(View.GONE);
    }

    void show(View view) {
        if (view != null)
            view.setVisibility(View.VISIBLE);
    }

    @NonNull
    Bitmap getBitmapFromDrawable(@NonNull Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        final Bitmap bmp = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bmp);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bmp;
    }

    void paintTextView(int color, TextView textView) {
        if (textView != null)
            textView.setTextColor(color);
    }

    void paintButton(int color, Button button) {
        if (button != null)
            ViewCompat.setBackgroundTintList(button, ColorStateList.valueOf(color));
    }

    void paintLayout(int color, int viewID) {
        RelativeLayout layout = view.findViewById(viewID);
        if (layout != null)
            ViewCompat.setBackgroundTintList(layout, ColorStateList.valueOf(color));
    }

    void paintLLayout(int color, int viewID) {
        LinearLayout layout = view.findViewById(viewID);
        if (layout != null)
            ViewCompat.setBackgroundTintList(layout, ColorStateList.valueOf(ColorUtils.setAlphaComponent(color, 50)));
    }
}
