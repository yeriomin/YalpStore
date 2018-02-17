package com.github.yeriomin.yalpstore.view;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;


abstract public class IntentOnClickListener implements View.OnClickListener {

    protected Context context;

    abstract protected Intent buildIntent();

    public IntentOnClickListener(Context context) {
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        try {
            Intent intent = buildIntent();
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            }
        } catch (ActivityNotFoundException e) {
            onActivityNotFound(e);
        }
    }

    protected void onActivityNotFound(ActivityNotFoundException e) {
        Log.e(getClass().getSimpleName(), "Could not find activity for intent: " + e.getMessage());
    }
}
