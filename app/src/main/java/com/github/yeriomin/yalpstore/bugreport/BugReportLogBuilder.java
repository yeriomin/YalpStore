package com.github.yeriomin.yalpstore.bugreport;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class BugReportLogBuilder extends BugReportBuilder {

    public BugReportLogBuilder(Context context) {
        super(context);
        setFileName("log.txt");
    }

    @Override
    public BugReportLogBuilder build() {
        StringBuilder result = new StringBuilder();
        Process logcat;
        try {
            logcat = Runtime.getRuntime().exec(new String[]{"logcat", "-d"});
            BufferedReader br = new BufferedReader(new InputStreamReader(logcat.getInputStream()), 4 * 1024);
            String line;
            while ((line = br.readLine()) != null) {
                result.append(line).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        setContent(result.toString());
        super.build();
        return this;
    }
}
