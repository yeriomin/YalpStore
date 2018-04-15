package in.dragons.galaxy.fragment.details;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.ViewCompat;
import android.support.v7.graphics.Palette;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.percolate.caffeine.ViewUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import in.dragons.galaxy.CategoryManager;
import in.dragons.galaxy.R;
import in.dragons.galaxy.Util;
import in.dragons.galaxy.activities.DetailsActivity;
import in.dragons.galaxy.model.App;
import in.dragons.galaxy.model.ImageSource;

public class GeneralDetails extends Abstract {

    private TextView appInfo;
    private TextView appReviews;
    private TextView appExtras;

    public GeneralDetails(DetailsActivity activity, App app) {
        super(activity, app);
    }

    @Override
    public void draw() {
        drawAppBadge(app);
        if (app.isInPlayStore()) {
            drawGeneralDetails(app);
            drawDescription(app);
        }
    }

    private void drawAppBadge(App app) {
        ImageView imageView = activity.findViewById(R.id.icon);
        RelativeLayout relativeLayout = activity.findViewById(R.id.details_header);
        ImageSource imageSource = app.getIconInfo();
        if (null != imageSource.getApplicationInfo()) {
            imageView.setImageDrawable(imageView.getContext().getPackageManager().getApplicationIcon(imageSource.getApplicationInfo()));
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            if (bitmap != null)
                getPalette(bitmap);
        } else {
            Picasso
                    .with(activity)
                    .load(imageSource.getUrl())
                    .placeholder(R.color.transparent)
                    .into(imageView, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                            if (bitmap != null)
                                getPalette(bitmap);
                        }

                        @Override
                        public void onError() {
                            relativeLayout.setBackgroundColor(Color.GRAY);
                        }
                    });
        }

        setText(R.id.displayName, app.getDisplayName());
        setText(R.id.packageName, R.string.details_developer, app.getDeveloperName());
        drawVersion(activity.findViewById(R.id.versionString), app);
    }

    private void getPalette(Bitmap bitmap) {
        Palette.from(bitmap)
                .generate(palette -> {
                    paintEmAll(palette.getVibrantColor(Color.DKGRAY));
                });
    }

    private void paintEmAll(int color) {
        paintRLayout(color, R.id.details_header);
        paintButton(color, R.id.download);
        paintButton(color, R.id.install);
        paintButton(color, R.id.run);
        paintButton(color, R.id.beta_subscribe_button);
        paintButton(color, R.id.beta_submit_button);
        paintTextView(color, R.id.beta_header);
        paintTextView(color, R.id.permissions_header);
        paintTextView(color, R.id.readMore);
    }

    private void paintButton(int color, int buttonId) {
        android.widget.Button button = activity.findViewById(buttonId);
        if (button != null)
            ViewCompat.setBackgroundTintList(button, ColorStateList.valueOf(color));
    }

    private void paintRLayout(int color, int layoutId) {
        RelativeLayout relativeLayout = activity.findViewById(layoutId);
        if (relativeLayout != null)
            relativeLayout.setBackgroundColor(color);
    }

    private void paintTextView(int color, int textViewId) {
        TextView textView = activity.findViewById(textViewId);
        if (textView != null)
            textView.setTextColor(color);
    }

    private void drawGeneralDetails(App app) {
        activity.findViewById(R.id.app_detail).setVisibility(View.VISIBLE);
        activity.findViewById(R.id.general_card).setVisibility(View.VISIBLE);

        if (app.isEarlyAccess()) {
            setText(R.id.rating, R.string.early_access);
        } else {
            setText(R.id.rating, R.string.details_rating, app.getRating().getAverage());
        }

        setText(R.id.installs, R.string.details_installs, Util.addDiPrefix(app.getInstalls()));
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

        appInfo = activity.findViewById(R.id.d_app_info);
        appReviews = activity.findViewById(R.id.d_reviews);
        appExtras = activity.findViewById(R.id.d_additional_info);

        appInfo.setOnClickListener(v -> {
            textViewHopper(appInfo, appReviews, appExtras);
            contentViewHopper(R.id.app_info_layout, R.id.reviews_layout, R.id.additional_info_layout);
        });
        appReviews.setOnClickListener(v -> {
            textViewHopper(appReviews, appExtras, appInfo);
            contentViewHopper(R.id.reviews_layout, R.id.app_info_layout, R.id.additional_info_layout);
        });
        appExtras.setOnClickListener(v -> {
            textViewHopper(appExtras, appReviews, appInfo);
            contentViewHopper(R.id.additional_info_layout, R.id.app_info_layout, R.id.reviews_layout);
        });
    }

    private void drawChanges(App app) {
        String changes = app.getChanges();
        TextView readMore = activity.findViewById(R.id.readMore);
        LinearLayout changelogLayout = activity.findViewById(R.id.changelog_container);
        if (TextUtils.isEmpty(changes)) {
            activity.findViewById(R.id.changes_container).setVisibility(View.GONE);
            return;
        }
        if (app.getInstalledVersionCode() == 0) {
            readMore.setVisibility(View.VISIBLE);
            setText(R.id.d_moreinf, Html.fromHtml(app.getDescription()).toString());
            readMore.setOnClickListener(v -> {
                if (changelogLayout.getVisibility() == View.GONE)
                    changelogLayout.setVisibility(View.VISIBLE);
                else
                    changelogLayout.setVisibility(View.GONE);
            });

        } else {
            setText(R.id.changes_upper, Html.fromHtml(changes).toString());
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
            activity.findViewById(R.id.more_card).setVisibility(View.GONE);
        } else {
            activity.findViewById(R.id.more_card).setVisibility(View.VISIBLE);
            setText(R.id.d_moreinf, Html.fromHtml(app.getDescription()).toString());
        }
    }

    private void textViewHopper(TextView A, TextView B, TextView C) {
        A.setTextColor(Color.BLACK);
        A.setAlpha(1.0f);
        B.setAlpha(0.5f);
        C.setAlpha(0.5f);
    }

    private void contentViewHopper(int viewA, int viewB, int viewC) {
        ViewUtils.findViewById(activity, viewA).setVisibility(View.VISIBLE);
        ViewUtils.findViewById(activity, viewB).setVisibility(View.GONE);
        ViewUtils.findViewById(activity, viewC).setVisibility(View.GONE);
    }
}