package com.dragons.aurora.fragment.details;

import android.content.Context;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dragons.aurora.R;
import com.dragons.aurora.fragment.DetailsFragment;
import com.dragons.aurora.fragment.PreferenceFragment;
import com.dragons.aurora.model.App;
import com.percolate.caffeine.PhoneUtils;
import com.percolate.caffeine.ViewUtils;

public abstract class AbstractHelper {

    protected DetailsFragment fragment;
    protected App app;

    public AbstractHelper(DetailsFragment fragment, App app) {
        this.fragment = fragment;
        this.app = app;
    }

    abstract public void draw();

    protected void setText(int viewId, String text) {
        TextView textView = (TextView) fragment.getActivity().findViewById(viewId);
        if (null != textView)
            textView.setText(text);
    }

    protected void setText(int viewId, int stringId, Object... text) {
        setText(viewId, fragment.getString(stringId, text));
    }

    protected void initExpandableGroup(int viewIdHeader, int viewIdContainer, final View.OnClickListener l) {
        TextView viewHeader = (TextView) fragment.getActivity().findViewById(viewIdHeader);
        viewHeader.setVisibility(View.VISIBLE);
        final LinearLayout viewContainer = (LinearLayout) fragment.getActivity().findViewById(viewIdContainer);
        viewHeader.setOnClickListener(v -> {
            boolean isExpanded = viewContainer.getVisibility() == View.VISIBLE;
            if (isExpanded) {
                viewContainer.setVisibility(View.GONE);
                ((TextView) v).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand_more, 0);
            } else {
                if (null != l) {
                    l.onClick(v);
                }
                viewContainer.setVisibility(View.VISIBLE);
                ((TextView) v).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand_less, 0);
            }
        });
    }

    protected boolean isLoggedIn() {
        return PreferenceFragment.getBoolean(fragment.getActivity(), "LOGGED_IN");
    }

    protected boolean isDummy() {
        return PreferenceFragment.getBoolean(fragment.getActivity(), "DUMMY_ACC");
    }

    protected boolean isGoogle() {
        return PreferenceFragment.getBoolean(fragment.getActivity(), "GOOGLE_ACC");
    }

    protected boolean isConnected(Context c) {
        return PhoneUtils.isNetworkAvailable(c);
    }

    protected void checkOut() {
        PreferenceManager.getDefaultSharedPreferences(fragment.getActivity()).edit().putBoolean("LOGGED_IN", false).apply();
        PreferenceManager.getDefaultSharedPreferences(fragment.getActivity()).edit().putString("GOOGLE_NAME", "").apply();
        PreferenceManager.getDefaultSharedPreferences(fragment.getActivity()).edit().putString("GOOGLE_URL", "").apply();
    }

    protected void hide(View v, int viewID) {
        ViewUtils.findViewById(v, viewID).setVisibility(View.GONE);
    }

    protected void show(View v, int viewID) {
        ViewUtils.findViewById(v, viewID).setVisibility(View.VISIBLE);
    }

    protected void setText(View v, int viewId, String text) {
        TextView textView = ViewUtils.findViewById(v, viewId);
        if (null != textView)
            textView.setText(text);
    }

    protected void setText(View v, int viewId, int stringId, Object... text) {
        setText(v, viewId, v.getResources().getString(stringId, text));
    }
}
