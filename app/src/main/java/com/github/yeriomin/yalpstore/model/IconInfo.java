package com.github.yeriomin.yalpstore.model;

import android.content.pm.ApplicationInfo;

public class IconInfo {

    private String url;
    private ApplicationInfo applicationInfo;

    public void setUrl(String url) {
        this.url = url;
    }

    public void setApplicationInfo(ApplicationInfo applicationInfo) {
        this.applicationInfo = applicationInfo;
    }

    public String getUrl() {
        return url;
    }

    public ApplicationInfo getApplicationInfo() {
        return applicationInfo;
    }
}
