package com.github.yeriomin.yalpstore.view;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class UriOnClickListener extends IntentOnClickListener {

    private String uriString;

    public UriOnClickListener(Context context, String uriString) {
        super(context);
        this.uriString = uriString;
    }

    @Override
    protected Intent buildIntent() {
        return new Intent(Intent.ACTION_VIEW, Uri.parse(uriString));
    }

    @Override
    protected void onActivityNotFound(ActivityNotFoundException e) {
        Log.e(getClass().getSimpleName(), "Could not find activity for uri " + uriString);
    }
}
