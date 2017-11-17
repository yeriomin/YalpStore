package com.github.yeriomin.yalpstore.bugreport;

import android.content.Context;

import java.util.Map;

public class BugReportPropertiesBuilder extends BugReportBuilder {

    public BugReportPropertiesBuilder(Context context) {
        super(context);
    }

    static protected String buildProperties(Map<String, String> properties) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String key: properties.keySet()) {
            stringBuilder
                .append(key)
                .append(" = ")
                .append(String.valueOf(properties.get(key)))
                .append("\n")
            ;
        }
        return stringBuilder.toString();
    }
}
