package com.github.yeriomin.yalpstore;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;

import com.github.yeriomin.yalpstore.model.App;

public class DetailsButtonUninstallFragment extends DetailsButtonFragment {

    public DetailsButtonUninstallFragment(DetailsActivity activity, App app) {
        super(activity, app);
    }

    @Override
    protected Button getButton() {
        return (Button) activity.findViewById(R.id.uninstall);
    }

    @Override
    protected boolean shouldBeVisible() {
        return app.isInstalled();
    }

    @Override
    protected View.OnClickListener getOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(Intent.ACTION_DELETE, Uri.parse("package:" + app.getPackageName())));
            }
        };
    }
}
