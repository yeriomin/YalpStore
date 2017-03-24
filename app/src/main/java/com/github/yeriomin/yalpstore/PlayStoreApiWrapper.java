package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.github.yeriomin.playstoreapi.AndroidAppDeliveryData;
import com.github.yeriomin.playstoreapi.BrowseLink;
import com.github.yeriomin.playstoreapi.BrowseResponse;
import com.github.yeriomin.playstoreapi.BulkDetailsEntry;
import com.github.yeriomin.playstoreapi.DeliveryResponse;
import com.github.yeriomin.playstoreapi.DetailsResponse;
import com.github.yeriomin.playstoreapi.DocV2;
import com.github.yeriomin.playstoreapi.GooglePlayAPI;
import com.github.yeriomin.playstoreapi.ReviewResponse;
import com.github.yeriomin.playstoreapi.SearchIterator;
import com.github.yeriomin.playstoreapi.SearchSuggestEntry;
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
 * so this warpper manages anything android-related and feeds it to the Akdeniz's classes
 * Specifically: credentials via Preferences, downloads via DownloadManager, app details using
 * android PackageInfo
 */
public class PlayStoreApiWrapper {

    private static final String BACKEND_DOCID_SIMILAR_APPS = "similar_apps";
    private static final String BACKEND_DOCID_USERS_ALSO_INSTALLED = "users_also_installed";

    private static AppSearchResultIterator searchResultIterator;
    private static CategoryAppsIterator categoryAppsIterator;

    private Context context;

    public PlayStoreApiWrapper(Context context) {
        this.context = context;
        AppBuilder.suffixMil = context.getString(R.string.suffix_million);
        AppBuilder.suffixBil = context.getString(R.string.suffix_billion);
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
        for (DocV2 doc: response.getDocV2().getChildList()) {
            boolean isSimilarApps = doc.getBackendDocid().contains(BACKEND_DOCID_SIMILAR_APPS);
            boolean isUsersAlsoInstalled = doc.getBackendDocid().contains(BACKEND_DOCID_USERS_ALSO_INSTALLED);
            if (isUsersAlsoInstalled && app.getUsersAlsoInstalledApps().size() > 0) {
                // Two users_also_installed lists are returned, consisting of mostly the same apps
                continue;
            }
            for (DocV2 child: doc.getChildList()) {
                if (isSimilarApps) {
                    app.getSimilarApps().add(AppBuilder.build(child));
                } else if (isUsersAlsoInstalled) {
                    app.getUsersAlsoInstalledApps().add(AppBuilder.build(child));
                }
            }
        }
        return app;
    }

    public List<App> getDetails(List<String> packageIds) throws IOException {
        List<App> apps = new ArrayList<>();
        int i = -1;
        boolean hideNonFree = hideNonFree();
        for (BulkDetailsEntry details: new PlayStoreApiAuthenticator(context).getApi().bulkDetails(packageIds).getEntryList()) {
            i++;
            if (!details.hasDoc()) {
                Log.i(this.getClass().getName(), "Empty response for " + packageIds.get(i));
                continue;
            }
            App app = AppBuilder.build(details.getDoc());
            if (hideNonFree && !app.isFree()) {
                Log.i(this.getClass().getName(), "Skipping non-free app " + packageIds.get(i));
                continue;
            }
            apps.add(app);
        }
        Collections.sort(apps);
        return apps;
    }

    public AppSearchResultIterator getSearchIterator(String query, String categoryId) throws IOException {
        if (TextUtils.isEmpty(query)) {
            Log.w(this.getClass().getName(), "Query empty, so don't expect meaningful results");
        }
        if (null == searchResultIterator
            || !searchResultIterator.getQuery().equals(query)
            || !searchResultIterator.getCategoryId().equals(categoryId)
        ) {
            searchResultIterator = new AppSearchResultIterator(new SearchIterator(new PlayStoreApiAuthenticator(context).getApi(), query));
            searchResultIterator.setHideNonfreeApps(hideNonFree());
            searchResultIterator.setCategoryId(categoryId);
        }
        return searchResultIterator;
    }

    public CategoryAppsIterator getCategoryAppsIterator(String categoryId) throws IOException {
        if (null == categoryAppsIterator
            || !categoryAppsIterator.getCategoryId().equals(categoryId)
        ) {
            categoryAppsIterator = new CategoryAppsIterator(
                new com.github.yeriomin.playstoreapi.CategoryAppsIterator(
                    new PlayStoreApiAuthenticator(context).getApi(),
                    categoryId,
                    GooglePlayAPI.SUBCATEGORY.TOP_FREE
                )
            );
        }
        return categoryAppsIterator;
    }

    public List<String> getSearchSuggestions(String query) throws IOException {
        List<String> suggestions = new ArrayList<>();
        for (SearchSuggestEntry suggestion: new PlayStoreApiAuthenticator(context).getApi().searchSuggest(query).getEntryList()) {
            suggestions.add(suggestion.getSuggestedQuery());
        }
        return suggestions;
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

    public AndroidAppDeliveryData purchaseOrDeliver(App app) throws IOException, NotPurchasedException {
        if (app.isFree()) {
            return new PlayStoreApiAuthenticator(context).getApi()
                .purchase(app.getPackageName(), app.getVersionCode(), app.getOfferType())
                .getPurchaseStatusResponse()
                .getAppDeliveryData();
        }
        DeliveryResponse response = new PlayStoreApiAuthenticator(context).getApi().delivery(
            app.getPackageName(), app.getVersionCode(), app.getOfferType());
        if (response.hasAppDeliveryData()) {
            return response.getAppDeliveryData();
        } else {
            throw new NotPurchasedException();
        }
    }

    private boolean hideNonFree() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(PreferenceActivity.PREFERENCE_HIDE_NONFREE_APPS, false);
    }
}
