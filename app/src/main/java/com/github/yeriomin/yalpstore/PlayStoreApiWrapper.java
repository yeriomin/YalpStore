package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
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
import com.github.yeriomin.playstoreapi.SearchSuggestEntry;
import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.model.AppBuilder;
import com.github.yeriomin.yalpstore.model.Review;
import com.github.yeriomin.yalpstore.model.ReviewBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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

    private Context context;
    private String email;
    private String password;

    private static GooglePlayAPI api;
    private static AppSearchResultIterator searchResultIterator;

    private GooglePlayAPI getApi() throws IOException {
        if (api == null) {
            api = buildApi();
        }
        return api;
    }

    private GooglePlayAPI constructApi() throws IOException {
        NativeDeviceInfoProvider deviceInfoProvider = new NativeDeviceInfoProvider();
        deviceInfoProvider.setContext(context);
        deviceInfoProvider.setLocaleString(Locale.getDefault().toString());
        GooglePlayAPI api = new GooglePlayAPI(email);
        api.setDeviceInfoProvider(deviceInfoProvider);
        api.setLocale(Locale.getDefault());
        return api;
    }

    private GooglePlayAPI buildApi() throws IOException {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String email = this.email == null ? prefs.getString(PreferenceActivity.PREFERENCE_EMAIL, "") : this.email;
        String gsfId = prefs.getString(PreferenceActivity.PREFERENCE_GSF_ID, "");
        String token = prefs.getString(PreferenceActivity.PREFERENCE_AUTH_TOKEN, "");
        if (email.isEmpty()) {
            throw new CredentialsEmptyException();
        }

        GooglePlayAPI api = constructApi();

        SharedPreferences.Editor prefsEditor = prefs.edit();
        boolean needToUploadDeviceConfig = false;
        if (gsfId.isEmpty()) {
            needToUploadDeviceConfig = true;
            String ac2dmToken = null == password ? TokenDispenser.getTokenAc2dm(email) : api.getAC2DMToken(password);
            gsfId = api.getGsfId(ac2dmToken);
            prefsEditor.putString(PreferenceActivity.PREFERENCE_GSF_ID, gsfId);
            prefsEditor.apply();
        }
        api.setGsfId(gsfId);
        if (token.isEmpty()) {
            token = null == password ? TokenDispenser.getToken(email) : api.getToken(password);
            prefsEditor.putString(PreferenceActivity.PREFERENCE_EMAIL, email);
            prefsEditor.putString(PreferenceActivity.PREFERENCE_AUTH_TOKEN, token);
            prefsEditor.apply();
        }
        api.setToken(token);
        if (needToUploadDeviceConfig) {
            api.uploadDeviceConfig();
        }
        return api;
    }

    public PlayStoreApiWrapper(Context context) {
        this.context = context;
        AppBuilder.suffixMil = context.getString(R.string.suffix_million);
        AppBuilder.suffixBil = context.getString(R.string.suffix_billion);
    }

    public GooglePlayAPI login(String email) throws IOException {
        this.email = email;
        PlayStoreApiWrapper.api = null;
        return getApi();
    }

    public GooglePlayAPI login(String email, String password) throws IOException {
        this.password = password;
        return login(email);
    }

    public void logout() {
        this.email = null;
        this.password = null;
        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(context).edit();
        prefs.remove(PreferenceActivity.PREFERENCE_EMAIL);
        prefs.remove(PreferenceActivity.PREFERENCE_GSF_ID);
        prefs.remove(PreferenceActivity.PREFERENCE_AUTH_TOKEN);
        prefs.apply();
        PlayStoreApiWrapper.api = null;
    }

    public void forceTokenRefresh() {
        this.password = null;
        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(context).edit();
        prefs.remove(PreferenceActivity.PREFERENCE_AUTH_TOKEN);
        prefs.apply();
        PlayStoreApiWrapper.api = null;
    }

    public List<Review> getReviews(String packageId, int offset, int numberOfResults) throws IOException {
        List<Review> reviews = new ArrayList<>();
        for (com.github.yeriomin.playstoreapi.Review review: getApi().reviews(
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
        ReviewResponse response = getApi().addOrEditReview(
            packageId,
            inputReview.getComment(),
            inputReview.getTitle(),
            inputReview.getRating()
        );
        return ReviewBuilder.build(response.getUserReview());
    }

    public void deleteReview(String packageId) throws IOException {
        getApi().deleteReview(packageId);
    }

    public App getDetails(String packageId) throws IOException {
        DetailsResponse response = getApi().details(packageId);
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
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean hideNonFree = sharedPreferences.getBoolean(PreferenceActivity.PREFERENCE_HIDE_NONFREE_APPS, false);
        for (BulkDetailsEntry details: getApi().bulkDetails(packageIds).getEntryList()) {
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
        Collections.sort(apps, new Comparator<App>() {
            @Override
            public int compare(App o1, App o2) {
                return o1.getDisplayName().compareToIgnoreCase(o2.getDisplayName());
            }
        });
        return apps;
    }

    public AppSearchResultIterator getSearchIterator(String query, String categoryId) throws IOException {
        if (null == query || query.isEmpty()) {
            Log.w(this.getClass().getName(), "Query empty, so don't expect meaningful results");
        }
        if (null == searchResultIterator
            || !searchResultIterator.getQuery().equals(query)
            || !searchResultIterator.getCategoryId().equals(categoryId)
        ) {
            searchResultIterator = new AppSearchResultIterator(getApi().getSearchIterator(query));
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            boolean hideNonFree = sharedPreferences.getBoolean(PreferenceActivity.PREFERENCE_HIDE_NONFREE_APPS, false);
            searchResultIterator.setHideNonfreeApps(hideNonFree);
            searchResultIterator.setCategoryId(categoryId);
        }
        return searchResultIterator;
    }

    public List<String> getSearchSuggestions(String query) throws IOException {
        List<String> suggestions = new ArrayList<>();
        for (SearchSuggestEntry suggestion: api.searchSuggest(query).getEntryList()) {
            suggestions.add(suggestion.getSuggestedQuery());
        }
        return suggestions;
    }

    public Map<String, String> getCategories() throws IOException {
        return buildCategoryMap(getApi().categories());
    }

    public Map<String, String> getCategories(String category) throws IOException {
        return buildCategoryMap(getApi().categories(category));
    }

    private Map<String, String> buildCategoryMap(BrowseResponse response) {
        Map<String, String> categories = new HashMap<>();
        for (BrowseLink category: response.getCategoryContainer().getCategoryList()) {
            String categoryId = Uri.parse(category.getDataUrl()).getQueryParameter("cat");
            if (null == categoryId || categoryId.isEmpty()) {
                continue;
            }
            categories.put(categoryId, category.getName());
        }
        return categories;
    }

    public AndroidAppDeliveryData purchaseOrDeliver(App app) throws IOException, NotPurchasedException {
        if (app.isFree()) {
            return getApi()
                .purchase(app.getPackageName(), app.getVersionCode(), app.getOfferType())
                .getPurchaseStatusResponse()
                .getAppDeliveryData();
        }
        DeliveryResponse response = getApi().delivery(app.getPackageName(), app.getVersionCode(), app.getOfferType());
        if (response.hasAppDeliveryData()) {
            return response.getAppDeliveryData();
        } else {
            throw new NotPurchasedException();
        }
    }
}
