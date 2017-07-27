package com.github.yeriomin.yalpstore;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class SelfUpdateChecker {

    static public int getLatestVersionCode() {
        int latestVersionCode = BuildConfig.VERSION_CODE;
        while (isAvailable(latestVersionCode + 1)) {
            latestVersionCode++;
        }
        return latestVersionCode;
    }

    static public String getUrlString(int versionCode) {
        return "https://f-droid.org/repo/com.github.yeriomin.yalpstore_" + versionCode + ".apk";
    }

    static private URL getUrl(int versionCode) {
        try {
            return new URL(getUrlString(versionCode));
        } catch (MalformedURLException e) {
            // Unlikely
        }
        return null;
    }

    static private boolean isAvailable(int versionCode) {
        try {
            URLConnection connection = getUrl(versionCode).openConnection();
            if (connection instanceof HttpURLConnection) {
                ((HttpURLConnection) connection).setRequestMethod("HEAD");
                return ((HttpURLConnection) connection).getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST;
            }
            InputStream in = connection.getInputStream();
            in.close();
            return true;
        } catch (IOException x) {
            return false;
        }
    }
}
