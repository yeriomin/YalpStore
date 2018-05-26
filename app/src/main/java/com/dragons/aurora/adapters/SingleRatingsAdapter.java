package com.dragons.aurora.adapters;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dragons.aurora.R;
import com.github.florent37.shapeofview.shapes.CircleView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class SingleRatingsAdapter extends RecyclerView.Adapter<SingleRatingsAdapter.ViewHolder> {

    private static SingleClickListener rClickListener;
    private static int rSelected = -1;
    private static boolean isFirstRating = true;

    private ViewHolder viewHolder;
    private Context context;
    private String[] mLabels;
    private String[] mValues;
    private Integer dotColor;
    private Integer[] mDotColors = {
            R.color.colorGreen,
            R.color.colorRed,
            R.color.colorOrange,
            R.color.colorPurple,
            R.color.colorGold,
            R.color.colorGreenAlt,
            R.color.colorCyan,
            R.color.colorBlue,
            R.color.colorLime,
            R.color.colorPink
    };

    public SingleRatingsAdapter(Context context, String[] mLabels, String[] mValues) {
        this.context = context;
        this.mLabels = mLabels;
        this.mValues = mValues;
    }

    private void setViewHolder(ViewHolder viewHolder) {
        this.viewHolder = viewHolder;
    }

    public void setOnRatingBadgeClickListener(SingleClickListener clickListener) {
        rClickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_badge, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        setViewHolder(holder);
        dotColor = viewHolder.itemView.getResources().getColor(mDotColors[position % mDotColors.length]);
        viewHolder.badgeText.setText(mLabels[position]);
        viewHolder.badgeDot.setBackgroundColor(dotColor);
        if (isFirstRating && isLastPref(position)) {
            toggleBadge(true);
            isFirstRating = false;
        } else
            setupBadge(position);
    }

    @Override
    public int getItemCount() {
        return mValues.length;
    }

    private void setupBadge(int position) {
        if (rSelected == position) {
            toggleBadge(true);
            putPref(Float.parseFloat(mValues[position]));
        } else {
            toggleBadge(false);
        }
    }

    private void toggleBadge(boolean toggle) {
        if (toggle) {
            viewHolder.badgeText.setTextColor(Color.WHITE);
            paintBadge(viewHolder);
        } else {
            viewHolder.badgeText.setTextColor(Color.BLACK);
            viewHolder.badgeContainer.setBackgroundTintList(null);
        }
    }

    private void paintBadge(ViewHolder holder) {
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), ColorUtils.setAlphaComponent(dotColor, 50), dotColor);
        colorAnimation.setDuration(350);
        colorAnimation.addUpdateListener(animator ->
                ViewCompat.setBackgroundTintList(holder.badgeContainer, ColorStateList.valueOf(
                        ColorUtils.setAlphaComponent((int) animator.getAnimatedValue(),
                                180))));
        colorAnimation.start();
    }

    private boolean isLastPref(int position) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getFloat("FILTER_RATING", 0.0f) == Float.parseFloat(mValues[position]);
    }

    private void putPref(float value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putFloat("FILTER_RATING", value).apply();
    }

    public interface SingleClickListener {
        void onRatingBadgeClickListener();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private RelativeLayout badgeContainer;
        private CircleView badgeDot;
        private CircleView badgeCancel;
        private TextView badgeText;

        public ViewHolder(View view) {
            super(view);
            badgeContainer = view.findViewById(R.id.badge_container);
            badgeDot = view.findViewById(R.id.badge_dot);
            badgeText = view.findViewById(R.id.badge_text);
            badgeCancel = view.findViewById(R.id.badge_cancel);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            rSelected = getAdapterPosition();
            rClickListener.onRatingBadgeClickListener();
        }
    }

}