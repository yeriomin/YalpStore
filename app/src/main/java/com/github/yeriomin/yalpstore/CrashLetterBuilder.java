package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.net.Uri;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

abstract public class CrashLetterBuilder {

    protected Context context;

    abstract protected String build();
    abstract protected File getFile();

    public CrashLetterBuilder(Context context) {
        this.context = context;
    }

    public Uri getUri() {
        try {
            File file = getFile();
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(build());
            bw.close();
            return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", file);
        } catch (IOException e) {
            return null;
        }
    }
}
