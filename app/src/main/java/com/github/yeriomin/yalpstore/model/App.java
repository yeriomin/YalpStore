package com.github.yeriomin.yalpstore.model;

import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

public class App {

    private PackageInfo packageInfo;

    private String displayName;
    private String versionName;
    private int versionCode;
    private Version version;
    private int offerType;
    private String updated;
    private long size;
    private String installs;
    private Rating rating = new Rating();
    private Drawable icon;
    private String iconUrl;
    private String changes;
    private Developer developer;
    private String description;
    private List<String> permissions;
    private boolean isInstalled;
    private boolean isFree;
    private List<String> screenshotUrls = new ArrayList<>();

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
        if (null != versionName && !versionName.isEmpty()) {
            this.version = new Version(versionName);
        }
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public Version getVersion() {
        return version;
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
        if (null == developer) {
            developer = new Developer();
        }
        return developer;
    }

    public void setDeveloper(Developer developer) {
        this.developer = developer;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
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
}
