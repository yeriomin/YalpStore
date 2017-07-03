package com.github.yeriomin.yalpstore;


import android.util.Log;

import com.github.yeriomin.yalpstore.model.App;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.zip.GZIPInputStream;

public class DeltaPatcherFactory {

    static public DeltaPatcherAbstract get(App app) {
        File patch = Paths.getDeltaPath(app.getPackageName(), app.getVersionCode());
        if (isGZipped(patch)) {
            return new DeltaPatcherGDiffGzipped(app);
        } else {
            return new DeltaPatcherGDiff(app);
        }
    }

    static private boolean isGZipped(File f) {
        int magic = 0;
        try {
            RandomAccessFile raf = new RandomAccessFile(f, "r");
            magic = raf.read() & 0xff | ((raf.read() << 8) & 0xff00);
            raf.close();
        } catch (IOException e) {
            Log.e(DeltaPatcherGDiff.class.getName(), "Could not check if patch is gzipped");
        }
        return magic == GZIPInputStream.GZIP_MAGIC;
    }
}
