package com.dragons.aurora;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import com.dragons.aurora.model.App;

public class DeltaPatcherGDiffGzipped extends DeltaPatcherGDiff {

    public DeltaPatcherGDiffGzipped(Context context, App app) {
        super(context, app);
    }

    @Override
    public boolean patch() {
        File patchUncompressed = new File(patch.getAbsolutePath() + ".unpacked");
        Log.i(DeltaPatcherGDiff.class.getSimpleName(), "Decompressing");
        File patchCompressed = patch;
        if (!GUnZip(patchCompressed, patchUncompressed)) {
            return false;
        }
        Log.i(DeltaPatcherGDiff.class.getSimpleName(), "Deleting " + patchCompressed);
        patchCompressed.delete();
        patch = patchUncompressed;
        return super.patch();
    }

    static private boolean GUnZip(File from, File to) {
        GZIPInputStream zipInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            zipInputStream = new GZIPInputStream(new FileInputStream(from));
            fileOutputStream = new FileOutputStream(to);
            byte[] buffer = new byte[0x1000];
            int count;
            while ((count = zipInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, count);
            }
            return true;
        } catch (IOException e) {
            Log.e(DeltaPatcherGDiff.class.getSimpleName(), "Could not unzip the patch: " + e.getMessage());
            return false;
        } finally {
            Util.closeSilently(fileOutputStream);
            Util.closeSilently(zipInputStream);
        }
    }
}
