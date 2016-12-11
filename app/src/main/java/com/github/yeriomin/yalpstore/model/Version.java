package com.github.yeriomin.yalpstore.model;

public class Version implements Comparable<Version> {

    private String version;

    public Version(String version) {
        this.version = normalize(version);
    }

    private String normalize(String version) {
        return version.replaceAll("[^\\d.]", "");
    }

    @Override
    public String toString() {
        return this.version;
    }

    @Override
    public int compareTo(Version other) {
        String[] thisParts = this.version.split("\\.");
        String[] otherParts = other.toString().split("\\.");
        for (int i = 0; i < Math.max(thisParts.length, otherParts.length); i++) {
            if (i >= thisParts.length) {
                return -1;
            } else if (i >= otherParts.length) {
                return 1;
            } else {
                Integer thisPart, otherPart;
                try {
                    thisPart = Integer.valueOf(thisParts[i]);
                } catch (NumberFormatException e) {
                    return -1;
                }
                try {
                    otherPart = Integer.valueOf(otherParts[i]);
                } catch (NumberFormatException e) {
                    return 1;
                }
                int comparison = thisPart.compareTo(otherPart);
                if (comparison != 0) {
                    return comparison;
                }
            }
        }
        return 0;
    }
}
