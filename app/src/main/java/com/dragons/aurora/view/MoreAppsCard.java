package com.dragons.aurora.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dragons.aurora.R;
import com.dragons.aurora.activities.CategoryAppsActivity;

public class MoreAppsCard extends RelativeLayout {

    String category;
    String label;
    TextView card_title;
    Button more_apps;

    public MoreAppsCard(Context context, String category, String label) {
        super(context);
        this.category = category;
        this.label = label;
        init(context);
    }

    public MoreAppsCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        View view = inflate(context, R.layout.more_apps_card, this);
        card_title = view.findViewById(R.id.m_apps_title);
        more_apps = view.findViewById(R.id.m_apps_more);
        card_title.setText(label);
        more_apps.setOnClickListener(v -> CategoryAppsActivity.start(context, category));
    }
}
