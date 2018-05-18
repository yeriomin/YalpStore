package com.dragons.aurora.fragment.details;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.dragons.aurora.R;
import com.dragons.aurora.activities.AuroraActivity;
import com.dragons.aurora.model.App;

public class ButtonRedirect extends Button {

    ButtonRedirect(AuroraActivity activity, App app) {
        super(activity, app);
    }

    @Override
    protected android.widget.Button getButton() {
        if (activity.findViewById(R.id.showInPlayStore).getVisibility() == View.VISIBLE)
            return null;
        else
            return (android.widget.Button) activity.findViewById(R.id.showInPlayStore);
    }

    @Override
    protected boolean shouldBeVisible() {
        return (app.getPrice() != null && !app.isFree());
    }

    @Override
    protected void onButtonClick(View v) {
        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + app.getPackageName())));
    }
}
