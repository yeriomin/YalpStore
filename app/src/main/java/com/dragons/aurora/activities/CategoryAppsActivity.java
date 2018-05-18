package com.dragons.aurora.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.dragons.aurora.CategoryManager;
import com.dragons.aurora.R;
import com.dragons.aurora.fragment.CategoryAppsFragment;
import com.dragons.aurora.view.AdaptiveToolbar;

public class CategoryAppsActivity extends AuroraActivity {

    static private final String INTENT_CATEGORY_ID = "INTENT_CATEGORY_ID";
    private String categoryId;

    static public Intent start(Context context, String categoryId) {
        Intent intent = new Intent(context, CategoryAppsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(INTENT_CATEGORY_ID, categoryId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.helper_activity_alt);
        onNewIntent(getIntent());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String newCategoryId = intent.getStringExtra(INTENT_CATEGORY_ID);
        if (null == newCategoryId) {
            Log.w(getClass().getSimpleName(), "No category id");
            return;
        }
        if (null == categoryId || !newCategoryId.equals(categoryId)) {
            categoryId = newCategoryId;
            setTitle(new CategoryManager(this).getCategoryName(categoryId));
            getCategoryApps(categoryId);
        }
        getCategoryApps(categoryId);
    }

    public void getCategoryApps(String categoryId) {
        CategoryAppsFragment categoryAppsFragment = new CategoryAppsFragment();
        Bundle arguments = new Bundle();
        arguments.putString("CategoryId", categoryId);
        categoryAppsFragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, categoryAppsFragment).commit();
    }
}
