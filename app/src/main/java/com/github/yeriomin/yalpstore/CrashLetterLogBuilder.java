package com.github.yeriomin.yalpstore;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class CrashLetterLogBuilder extends CrashLetterBuilder {

    public CrashLetterLogBuilder(Context context) {
        super(context);
    }

    @Override
    protected String build() {
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
        return result.toString();
    }

    @Override
    protected File getFile() {
        try {
            return File.createTempFile("log-", ".txt");
        } catch (IOException e) {
            return null;
        }
    }
}
