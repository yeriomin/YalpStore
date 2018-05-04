package com.dragons.aurora.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

public abstract class ListItem {

    protected View view;

    public void setView(View view) {
        this.view = view;
    }

    abstract public void draw();

    @NonNull
    protected Bitmap getBitmapFromDrawable(@NonNull Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        final Bitmap bmp = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bmp);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bmp;
    }

    protected void paintTextView(int color, int textViewId) {
        TextView textView = view.findViewById(textViewId);
        if (textView != null)
            textView.setTextColor(color);
    }
}
