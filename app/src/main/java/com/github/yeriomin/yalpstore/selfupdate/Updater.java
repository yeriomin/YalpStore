package com.github.yeriomin.yalpstore.selfupdate;

import com.github.yeriomin.yalpstore.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

abstract public class Updater {

    abstract public String getUrlString(int versionCode);

    public int getLatestVersionCode() {
        int latestVersionCode = BuildConfig.VERSION_CODE;
        while (isAvailable(latestVersionCode + 1)) {
            latestVersionCode++;
        }
        return latestVersionCode;
    }

    private URL getUrl(int versionCode) {
        try {
            return new URL(getUrlString(versionCode));
        } catch (MalformedURLException e) {
            // Unlikely
        }
        return null;
    }

    private boolean isAvailable(int versionCode) {
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
