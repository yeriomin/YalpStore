package com.dragons.aurora.view;

import android.content.Context;
import android.util.AttributeSet;

public class TopUtilityApps extends CustomRecycler {

    String JSON_PATH = "https://raw.githubusercontent.com/GalaxyStore/MetaData/master/top_tools.json";

    public TopUtilityApps(Context context) {
        super(context);
        init(context, null, 0);
    }

    public TopUtilityApps(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public TopUtilityApps(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        setHorizontalScrollBarEnabled(true);
        JsonParser(context, JSON_PATH);
    }
}
