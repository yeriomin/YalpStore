package com.github.yeriomin.yalpstore;

import org.xmlpull.v1.XmlPullParserException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FdroidLinkExtractor {

    static private final String LINK_PATTERN = "https://f-droid\\.org/repo/com\\.github\\.yeriomin\\.yalpstore_(\\d+)\\.apk";

    private int latestVersionCode;
    private String latestLink;

    public String getLatestLink() {
        return latestLink;
    }

    public int getLatestVersionCode() {
        return latestVersionCode;
    }

    public FdroidLinkExtractor(String html) throws XmlPullParserException {
        Pattern pattern = Pattern.compile(LINK_PATTERN);
        Matcher matcher = pattern.matcher(html);
        while (matcher.find()) {
            int versionCode = Integer.valueOf(matcher.group(1));
            if (versionCode > latestVersionCode) {
                latestVersionCode = versionCode;
                latestLink = html.substring(matcher.start(), matcher.end());
            }
        }
    }
}
