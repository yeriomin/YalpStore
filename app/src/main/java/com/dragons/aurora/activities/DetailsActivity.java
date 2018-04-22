package com.dragons.aurora.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.dragons.aurora.R;
import com.dragons.aurora.fragment.DetailsFragment;

public class DetailsActivity extends AuroraActivity {

    static private final String INTENT_PACKAGE_NAME = "INTENT_PACKAGE_NAME";

    static public Intent getDetailsIntent(Context context, String packageName) {
        Intent intent = new Intent(context, DetailsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(DetailsActivity.INTENT_PACKAGE_NAME, packageName);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.helper_activity_alt);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        onNewIntent(getIntent());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        final String packageName = getIntentPackageName(intent);
        if (TextUtils.isEmpty(packageName)) {
            Log.e(this.getClass().getName(), "No package name provided");
            finish();
            return;
        }
        Log.i(getClass().getSimpleName(), "Getting info about " + packageName);

        if (null != DetailsActivity.app) {
            redrawDetails(DetailsActivity.app);
        }

        grabDetails(packageName);
    }

    private String getIntentPackageName(Intent intent) {
        if (intent.hasExtra(INTENT_PACKAGE_NAME)) {
            return intent.getStringExtra(INTENT_PACKAGE_NAME);
        } else if (intent.getScheme() != null
                && (intent.getScheme().equals("market")
                || intent.getScheme().equals("http")
                || intent.getScheme().equals("https")
        )) {
            return intent.getData().getQueryParameter("id");
        }
        return null;
    }

    protected void grabDetails(String packageName) {
        DetailsFragment detailsFragment = new DetailsFragment();
        Bundle arguments = new Bundle();
        arguments.putString("PackageName", packageName);
        detailsFragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.content_frame, detailsFragment).commit();
    }
}
