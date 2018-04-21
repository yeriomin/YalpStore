package com.dragons.aurora.fragment.details;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dragons.aurora.R;
import com.dragons.aurora.fragment.DetailsFragment;
import com.dragons.aurora.model.App;

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
}
