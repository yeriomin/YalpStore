package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.github.yeriomin.playstoreapi.BrowseLink;
import com.github.yeriomin.playstoreapi.BrowseResponse;
import com.github.yeriomin.playstoreapi.BulkDetailsEntry;
import com.github.yeriomin.playstoreapi.DetailsResponse;
import com.github.yeriomin.playstoreapi.GooglePlayAPI;
import com.github.yeriomin.playstoreapi.ReviewResponse;
import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.model.AppBuilder;
import com.github.yeriomin.yalpstore.model.Review;
import com.github.yeriomin.yalpstore.model.ReviewBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Akdeniz Google Play Crawler classes are supposed to be independent from android,
 * so this wrapper manages anything android-related and feeds it to the Akdeniz's classes
 * Specifically: credentials via Preferences, downloads via DownloadManager, app details using
 * android PackageInfo
 */
public class PlayStoreApiWrapper {

    private Context context;

    public PlayStoreApiWrapper(Context context) {
        this.context = context;
    }

    public List<Review> getReviews(String packageId, int offset, int numberOfResults) throws IOException {
        List<Review> reviews = new ArrayList<>();
        for (com.github.yeriomin.playstoreapi.Review review: new PlayStoreApiAuthenticator(context).getApi().reviews(
            packageId,
            GooglePlayAPI.REVIEW_SORT.HELPFUL,
            offset,
            numberOfResults
        ).getGetResponse().getReviewList()) {
            reviews.add(ReviewBuilder.build(review));
        }
        return reviews;
    }

    public Review addOrEditReview(String packageId, Review inputReview) throws IOException {
        ReviewResponse response = new PlayStoreApiAuthenticator(context).getApi().addOrEditReview(
            packageId,
            inputReview.getComment(),
            inputReview.getTitle(),
            inputReview.getRating()
        );
        return ReviewBuilder.build(response.getUserReview());
    }

    public void deleteReview(String packageId) throws IOException {
        new PlayStoreApiAuthenticator(context).getApi().deleteReview(packageId);
    }

    public App getDetails(String packageId) throws IOException {
        DetailsResponse response = new PlayStoreApiAuthenticator(context).getApi().details(packageId);
        App app = AppBuilder.build(response.getDocV2());
        if (response.hasUserReview()) {
            app.setUserReview(ReviewBuilder.build(response.getUserReview()));
        }
        return app;
    }

    public List<App> getDetails(List<String> packageIds) throws IOException {
        List<App> apps = new ArrayList<>();
        for (BulkDetailsEntry details: new PlayStoreApiAuthenticator(context).getApi().bulkDetails(packageIds).getEntryList()) {
            if (!details.hasDoc()) {
                continue;
            }
            apps.add(AppBuilder.build(details.getDoc()));
        }
        Collections.sort(apps);
        return apps;
    }

    public Map<String, String> getCategories() throws IOException {
        return buildCategoryMap(new PlayStoreApiAuthenticator(context).getApi().categories());
    }

    public Map<String, String> getCategories(String category) throws IOException {
        return buildCategoryMap(new PlayStoreApiAuthenticator(context).getApi().categories(category));
    }

    private Map<String, String> buildCategoryMap(BrowseResponse response) {
        Map<String, String> categories = new HashMap<>();
        for (BrowseLink category: response.getCategoryContainer().getCategoryList()) {
            String categoryId = Uri.parse(category.getDataUrl()).getQueryParameter("cat");
            if (TextUtils.isEmpty(categoryId)) {
                continue;
            }
            categories.put(categoryId, category.getName());
        }
        return categories;
    }
}
