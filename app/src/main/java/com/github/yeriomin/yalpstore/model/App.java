package com.github.yeriomin.yalpstore.model;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class App implements Comparable<App> {

    private PackageInfo packageInfo;

    private String displayName;
    private String versionName;
    private int versionCode;
    private int offerType;
    private String updated;
    private long size;
    private String installs;
    private Rating rating = new Rating();
    private Drawable icon;
    private String iconUrl;
    private String changes;
    private Developer developer = new Developer();
    private String description;
    private Set<String> permissions;
    private boolean isInstalled;
    private boolean isFree;
    private List<String> screenshotUrls = new ArrayList<>();
    private Review userReview;
    private List<App> similarApps = new ArrayList<>();
    private List<App> usersAlsoInstalledApps = new ArrayList<>();
    private String categoryId;
    private String price;
    private boolean containsAds;
    private Set<String> dependencies = new HashSet<>();
    private Map<String, String> offerDetails = new HashMap<>();
    private boolean system;

    public App() {
        this.packageInfo = new PackageInfo();
    }

    public App(PackageInfo packageInfo) {
        this.setPackageInfo(packageInfo);
    }

    public PackageInfo getPackageInfo() {
        return packageInfo;
    }

    public String getPackageName() {
        return packageInfo.packageName;
    }

    public void setPackageInfo(PackageInfo packageInfo) {
        this.packageInfo = packageInfo;
        this.setVersionName(packageInfo.versionName);
        this.setVersionCode(packageInfo.versionCode);
        this.setSystem((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public int getOfferType() {
        return offerType;
    }

    public void setOfferType(int offerType) {
        this.offerType = offerType;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getInstalls() {
        return installs;
    }

    public void setInstalls(String installs) {
        this.installs = installs;
    }

    public Rating getRating() {
        return rating;
    }

    public Drawable getIcon() {
        return this.icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getIconUrl() {
        return this.iconUrl;
    }

    public String getChanges() {
        return changes;
    }

    public void setChanges(String changes) {
        this.changes = changes;
    }

    public Developer getDeveloper() {
        return developer;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Collection<String> permissions) {
        this.permissions = new HashSet<>(permissions);
    }

    public boolean isInstalled() {
        return isInstalled;
    }

    public void setInstalled(boolean installed) {
        isInstalled = installed;
    }

    public boolean isFree() {
        return isFree;
    }

    public void setFree(boolean free) {
        isFree = free;
    }

    public List<String> getScreenshotUrls() {
        return screenshotUrls;
    }

    public Review getUserReview() {
        return userReview;
    }

    public void setUserReview(Review userReview) {
        this.userReview = userReview;
    }

    public List<App> getSimilarApps() {
        return similarApps;
    }

    public List<App> getUsersAlsoInstalledApps() {
        return usersAlsoInstalledApps;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public boolean containsAds() {
        return containsAds;
    }

    public void setContainsAds(boolean containsAds) {
        this.containsAds = containsAds;
    }

    public Set<String> getDependencies() {
        return dependencies;
    }

    public Map<String, String> getOfferDetails() {
        return offerDetails;
    }

    public boolean isSystem() {
        return system;
    }

    public void setSystem(boolean system) {
        this.system = system;
    }

    @Override
    public int compareTo(App o) {
        return getDisplayName().compareToIgnoreCase(o.getDisplayName());
    }
}
