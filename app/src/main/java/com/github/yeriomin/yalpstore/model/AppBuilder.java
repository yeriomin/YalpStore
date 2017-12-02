package com.github.yeriomin.yalpstore.model;

import android.text.TextUtils;

import com.github.yeriomin.playstoreapi.AggregateRating;
import com.github.yeriomin.playstoreapi.AppDetails;
import com.github.yeriomin.playstoreapi.Dependency;
import com.github.yeriomin.playstoreapi.DocV2;
import com.github.yeriomin.playstoreapi.Image;
import com.github.yeriomin.playstoreapi.RelatedLink;
import com.github.yeriomin.playstoreapi.Unknown25Item;
import com.github.yeriomin.yalpstore.Util;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppBuilder {

    private static final int IMAGE_ICON = 4;
    private static final int IMAGE_VIDEO = 3;
    private static final int IMAGE_SCREENSHOT = 1;

    static public App build(DocV2 details) {
        App app = new App();
        app.setDisplayName(details.getTitle());
        app.setDescription(details.getDescriptionHtml());
        app.setCategoryId(details.getRelatedLinks().getCategoryInfo().getAppCategory());
        app.setRestriction(details.getAvailability().getRestriction());
        if (details.getOfferCount() > 0) {
            app.setOfferType(details.getOffer(0).getOfferType());
            app.setFree(details.getOffer(0).getMicros() == 0);
            app.setPrice(details.getOffer(0).getFormattedAmount());
        }
        fillOfferDetails(app, details);
        fillAggregateRating(app, details.getAggregateRating());
        fillRelatedLinks(app, details);
        AppDetails appDetails = details.getDetails().getAppDetails();
        app.getPackageInfo().packageName = appDetails.getPackageName();
        app.setVersionName(appDetails.getVersionString());
        app.setVersionCode(appDetails.getVersionCode());
        app.setDeveloperName(appDetails.getDeveloperName());
        app.setSize(appDetails.getInstallationSize());
        app.setInstalls(getInstallsNum(appDetails.getNumDownloads()));
        app.setUpdated(appDetails.getUploadDate());
        app.setChanges(appDetails.getRecentChangesHtml());
        app.setPermissions(appDetails.getPermissionList());
        app.setContainsAds(appDetails.hasContainsAds() && !TextUtils.isEmpty(appDetails.getContainsAds()));
        app.setInPlayStore(true);
        app.setEarlyAccess(appDetails.hasEarlyAccessInfo());
        app.setTestingProgramAvailable(appDetails.hasTestingProgramInfo());
        if (app.isTestingProgramAvailable()) {
            app.setTestingProgramOptedIn(appDetails.getTestingProgramInfo().hasSubscribed() && appDetails.getTestingProgramInfo().getSubscribed());
            app.setTestingProgramEmail(appDetails.getTestingProgramInfo().getTestingProgramEmail());
        }
        fillImages(app, details.getImageList());
        fillDependencies(app, appDetails);
        return app;
    }

    static private String getInstallsNum(String installsRaw) {
        Matcher matcher = Pattern.compile("[\\d]+").matcher(installsRaw.replaceAll("[,\\.\\s]+", ""));
        if (matcher.find()) {
            return Util.addSiPrefix(Util.parseInt(matcher.group(0), 0));
        }
        return null;
    }

    static private void fillAggregateRating(App app, AggregateRating aggregateRating) {
        Rating rating = app.getRating();
        rating.setAverage(aggregateRating.getStarRating());
        rating.setStars(1, (int) aggregateRating.getOneStarRatings());
        rating.setStars(2, (int) aggregateRating.getTwoStarRatings());
        rating.setStars(3, (int) aggregateRating.getThreeStarRatings());
        rating.setStars(4, (int) aggregateRating.getFourStarRatings());
        rating.setStars(5, (int) aggregateRating.getFiveStarRatings());
    }

    static private void fillDependencies(App app, AppDetails appDetails) {
        if (!appDetails.hasDependencies() || appDetails.getDependencies().getDependencyCount() == 0) {
            return;
        }
        for (Dependency dep: appDetails.getDependencies().getDependencyList()) {
            app.getDependencies().add(dep.getPackageName());
        }
    }

    static private void fillOfferDetails(App app, DocV2 details) {
        if (!details.hasUnknown25() || details.getUnknown25().getItemCount() == 0) {
            return;
        }
        for (Unknown25Item item: details.getUnknown25().getItemList()) {
            if (!item.hasContainer()) {
                continue;
            }
            app.getOfferDetails().put(item.getLabel(), item.getContainer().getValue());
        }
    }

    static private void fillRelatedLinks(App app, DocV2 details) {
        if (!details.hasRelatedLinks()) {
            return;
        }
        for (RelatedLink link: details.getRelatedLinks().getRelatedLinksList()) {
            if (!link.hasLabel() || !link.hasUrl1()) {
                continue;
            }
            app.getRelatedLinks().put(link.getLabel(), link.getUrl1());
        }
    }

    static private void fillImages(App app, List<Image> images) {
        for (Image image: images) {
            if (image.getImageType() == IMAGE_ICON) {
                app.setIconUrl(image.getImageUrl());
            } else if (image.getImageType() == IMAGE_VIDEO) {
                app.setVideoUrl(image.getImageUrl());
            } else if (image.getImageType() == IMAGE_SCREENSHOT) {
                app.getScreenshotUrls().add(image.getImageUrl());
            }
        }
    }
}
