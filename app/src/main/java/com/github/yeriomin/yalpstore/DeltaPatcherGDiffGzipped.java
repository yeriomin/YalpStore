package com.github.yeriomin.yalpstore;

import android.util.Log;

import com.github.yeriomin.yalpstore.model.App;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

public class DeltaPatcherGDiffGzipped extends DeltaPatcherGDiff {

    public DeltaPatcherGDiffGzipped(App app) {
        super(app);
    }

    @Override
    public boolean patch() {
        File patchUncompressed = new File(patch.getAbsolutePath() + ".unpacked");
        Log.i(DeltaPatcherGDiff.class.getName(), "Decompressing");
        File patchCompressed = patch;
        if (!GUnZip(patchCompressed, patchUncompressed)) {
            return false;
        }
        Log.i(DeltaPatcherGDiff.class.getName(), "Deleting " + patchCompressed);
        patchCompressed.delete();
        patch = patchUncompressed;
        return super.patch();
    }

    static private boolean GUnZip(File from, File to) {
        try {
            GZIPInputStream zipInputStream = new GZIPInputStream(new FileInputStream(from));
            FileOutputStream fileOutputStream = new FileOutputStream(to);
            byte[] buffer = new byte[0x1000];
            int count;
            while ((count = zipInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, count);
            }
            fileOutputStream.close();
            zipInputStream.close();
            return true;
        } catch (IOException e) {
            Log.e(DeltaPatcherGDiff.class.getName(), "Could not unzip the patch: " + e.getMessage());
            return false;
        }
    }
}
