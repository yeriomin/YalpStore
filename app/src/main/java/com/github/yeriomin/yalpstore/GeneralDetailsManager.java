package com.github.yeriomin.yalpstore;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.model.App;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GeneralDetailsManager extends DetailsManager {

    public GeneralDetailsManager(DetailsActivity activity, App app) {
        super(activity, app);
    }

    @Override
    public void draw() {
        drawGeneralDetails(app);
        drawDescription(app);
        drawPermissions(app);
        new GoogleDependencyManager(activity, app).draw();
    }

    private void drawGeneralDetails(App app) {
        ((ImageView) activity.findViewById(R.id.icon)).setImageDrawable(app.getIcon());

        setText(R.id.displayName, app.getDisplayName());
        setText(R.id.packageName, app.getPackageName());
        setText(R.id.installs, R.string.details_installs, app.getInstalls());
        setText(R.id.rating, R.string.details_rating, app.getRating().getAverage());
        setText(R.id.updated, R.string.details_updated, app.getUpdated());
        setText(R.id.size, R.string.details_size, Formatter.formatShortFileSize(activity, app.getSize()));
        setText(R.id.category, R.string.details_category, new CategoryManager(activity).getCategoryName(app.getCategoryId()));
        setText(R.id.price, app.getPrice());
        setText(R.id.contains_ads, app.containsAds() ? R.string.details_contains_ads : R.string.details_no_ads);
        drawOfferDetails(app);
        drawChanges(app);
        drawVersion((TextView) activity.findViewById(R.id.versionString), app);
        if (app.getVersionCode() == 0) {
            activity.findViewById(R.id.updated).setVisibility(View.GONE);
            activity.findViewById(R.id.size).setVisibility(View.GONE);
        }
    }

    private void drawChanges(App app) {
        String changes = app.getChanges();
        if (null != changes && !changes.isEmpty()) {
            setText(R.id.changes, Html.fromHtml(changes).toString());
            activity.findViewById(R.id.changes).setVisibility(View.VISIBLE);
            activity.findViewById(R.id.changes_title).setVisibility(View.VISIBLE);
        }
    }

    private void drawOfferDetails(App app) {
        List<String> keyList = new ArrayList<>(app.getOfferDetails().keySet());
        Collections.reverse(keyList);
        for (String key: keyList) {
            addOfferItem(key, app.getOfferDetails().get(key));
        }
    }

    private void addOfferItem(String key, String value) {
        TextView itemView = new TextView(activity);
        itemView.setAutoLinkMask(Linkify.ALL);
        itemView.setText(key + " " + Html.fromHtml(value));
        ((LinearLayout) activity.findViewById(R.id.offer_details)).addView(itemView);
    }

    private void drawVersion(TextView textView, App app) {
        String versionName = app.getVersionName();
        if (null == versionName || versionName.isEmpty()) {
            return;
        }
        textView.setText(activity.getString(R.string.details_versionName, versionName));
        textView.setVisibility(View.VISIBLE);
        if (!app.isInstalled()) {
            return;
        }
        try {
            PackageInfo info = activity.getPackageManager().getPackageInfo(app.getPackageName(), 0);
            if (info.versionCode != app.getVersionCode()) {
                textView.setText(activity.getString(R.string.details_versionName_updatable, info.versionName, versionName));
            }
        } catch (PackageManager.NameNotFoundException e) {
            // We've checked for that already
        }
    }

    private void drawDescription(App app) {
        setText(R.id.description, Html.fromHtml(app.getDescription()).toString());
        initExpandableGroup(R.id.description_header, R.id.description_container);
    }

    private void drawPermissions(App app) {
        initExpandableGroup(R.id.permissions_header, R.id.permissions_container);
        PackageManager pm = activity.getPackageManager();
        List<String> localizedPermissions = new ArrayList<>();
        for (String permissionName: app.getPermissions()) {
            try {
                localizedPermissions.add(pm.getPermissionInfo(permissionName, 0).loadLabel(pm).toString());
            } catch (PackageManager.NameNotFoundException e) {
                Log.w(getClass().getName(), "No human-readable name found for permission " + permissionName);
            }
        }
        setText(R.id.permissions, TextUtils.join("\n", localizedPermissions));
    }
}
