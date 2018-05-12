package com.dragons.aurora.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dragons.aurora.R;

public class ClusterAppsCard extends RelativeLayout {

    String label;
    TextView card_title;

    public ClusterAppsCard(Context context, String label) {
        super(context);
        this.label = label;
        init(context);
    }

    public ClusterAppsCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        View view = inflate(context, R.layout.cluster_apps_card, this);
        card_title = view.findViewById(R.id.m_apps_title);
        card_title.setText(label);
    }
}
