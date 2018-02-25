package in.dragons.galaxy.fragment.details;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import in.dragons.galaxy.CategoryManager;
import in.dragons.galaxy.DetailsActivity;
import in.dragons.galaxy.R;
import in.dragons.galaxy.Util;
import in.dragons.galaxy.model.App;
import in.dragons.galaxy.model.ImageSource;

public class GeneralDetails extends Abstract {

    public GeneralDetails(DetailsActivity activity, App app) {
        super(activity, app);
    }

    @Override
    public void draw() {
        drawAppBadge(app);
        if (app.isInPlayStore()) {
            drawGeneralDetails(app);
            drawDescription(app);
            new GoogleDependency((DetailsActivity) activity, app).draw();
        }
    }

    private void drawAppBadge(App app) {
        ImageView imageView = (ImageView) activity.findViewById(R.id.icon);
        ImageSource imageSource = app.getIconInfo();
        if (null != imageSource.getApplicationInfo()) {
            imageView.setImageDrawable(imageView.getContext().getPackageManager().getApplicationIcon(imageSource.getApplicationInfo()));
        } else {
            Picasso
                    .with(activity)
                    .load(imageSource.getUrl())
                    .placeholder(R.drawable.ic_placeholder)
                    .into(imageView);
        }

        setText(R.id.displayName, app.getDisplayName());
        setText(R.id.packageName, R.string.details_developer, app.getDeveloperName());
        drawVersion((TextView) activity.findViewById(R.id.versionString), app);
    }

    private void drawGeneralDetails(App app) {
        activity.findViewById(R.id.general_card).setVisibility(View.VISIBLE);
        activity.findViewById(R.id.general_details).setVisibility(View.VISIBLE);
        activity.findViewById(R.id.app_detail).setVisibility(View.VISIBLE);
        activity.findViewById(R.id.divider_top).setVisibility(View.VISIBLE);
        activity.findViewById(R.id.divider_bottom).setVisibility(View.VISIBLE);
        setText(R.id.installs, R.string.details_installs, Util.addSiPrefix(app.getInstalls()));
        if (app.isEarlyAccess()) {
            setText(R.id.rating, R.string.early_access);
        } else {
            setText(R.id.rating, R.string.details_rating, app.getRating().getAverage());
        }
        setText(R.id.updated, R.string.details_updated, app.getUpdated());
        setText(R.id.size, R.string.details_size, Formatter.formatShortFileSize(activity, app.getSize()));
        setText(R.id.category, R.string.details_category, new CategoryManager(activity).getCategoryName(app.getCategoryId()));
        setText(R.id.developer, R.string.details_developer, app.getDeveloperName());
        setText(R.id.price, app.getPrice());
        setText(R.id.contains_ads, app.containsAds() ? R.string.details_contains_ads : R.string.details_no_ads);
        drawOfferDetails(app);
        drawChanges(app);
        if (app.getVersionCode() == 0) {
            activity.findViewById(R.id.updated).setVisibility(View.GONE);
            activity.findViewById(R.id.size).setVisibility(View.GONE);
        }
    }

    private void drawChanges(App app) {
        String changes = app.getChanges();
        if (TextUtils.isEmpty(changes)) {
            activity.findViewById(R.id.changes).setVisibility(View.GONE);
            activity.findViewById(R.id.changes_title).setVisibility(View.GONE);
            activity.findViewById(R.id.changes_header).setVisibility(View.GONE);
            activity.findViewById(R.id.changes_container).setVisibility(View.GONE);
            activity.findViewById(R.id.changes_upper).setVisibility(View.GONE);
            return;
        }
        if (app.getInstalledVersionCode() == 0) {
            setText(R.id.changes, Html.fromHtml(changes).toString());
            activity.findViewById(R.id.changes).setVisibility(View.VISIBLE);
            activity.findViewById(R.id.changes_title).setVisibility(View.VISIBLE);
        } else {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
            activity.findViewById(R.id.changes_upper).setVisibility(View.VISIBLE);
            setText(R.id.changes_upper, Html.fromHtml(changes).toString());
            activity.findViewById(R.id.changes_header).setVisibility(View.VISIBLE);
            activity.findViewById(R.id.changes_container).setVisibility(View.VISIBLE);
        }
    }

    private void drawOfferDetails(App app) {
        List<String> keyList = new ArrayList<>(app.getOfferDetails().keySet());
        Collections.reverse(keyList);
        for (String key : keyList) {
            addOfferItem(key, app.getOfferDetails().get(key));
        }
    }

    private void addOfferItem(String key, String value) {
        if (null == value) {
            return;
        }
        TextView itemView = new TextView(activity);
        try {
            itemView.setAutoLinkMask(Linkify.ALL);
            itemView.setText(activity.getString(R.string.two_items, key, Html.fromHtml(value)));
        } catch (RuntimeException e) {
            Log.w(getClass().getSimpleName(), "System WebView missing: " + e.getMessage());
            itemView.setAutoLinkMask(0);
            itemView.setText(activity.getString(R.string.two_items, key, Html.fromHtml(value)));
        }
        ((LinearLayout) activity.findViewById(R.id.offer_details)).addView(itemView);
    }

    private void drawVersion(TextView textView, App app) {
        String versionName = app.getVersionName();
        if (TextUtils.isEmpty(versionName)) {
            return;
        }
        textView.setText(activity.getString(R.string.details_versionName, versionName));
        textView.setVisibility(View.VISIBLE);
        if (!app.isInstalled()) {
            return;
        }
        try {
            PackageInfo info = activity.getPackageManager().getPackageInfo(app.getPackageName(), 0);
            String currentVersion = info.versionName;
            if (info.versionCode == app.getVersionCode() || null == currentVersion) {
                return;
            }
            String newVersion = versionName;
            if (currentVersion.equals(newVersion)) {
                currentVersion += " (" + info.versionCode;
                newVersion = app.getVersionCode() + ")";
            }
            textView.setText(activity.getString(R.string.details_versionName_updatable, currentVersion, newVersion));
            setText(R.id.download, activity.getString(R.string.details_update));
        } catch (PackageManager.NameNotFoundException e) {
            // We've checked for that already
        }
    }

    private void drawDescription(App app) {
        if (TextUtils.isEmpty(app.getDescription())) {
            activity.findViewById(R.id.description_header).setVisibility(View.GONE);
        } else {
            setText(R.id.description, Html.fromHtml(app.getDescription()).toString());
            initExpandableGroup(R.id.description_header, R.id.description_container);
            activity.findViewById(R.id.more_card).setVisibility(View.VISIBLE);
            if (app.getInstalledVersionCode() == 0 || TextUtils.isEmpty(app.getChanges())) {
                //activity.findViewById(R.id.description_header).performClick();
            }
        }
    }
}
