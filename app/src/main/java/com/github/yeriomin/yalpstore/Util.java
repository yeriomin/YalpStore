/*
 * Yalp Store
 * Copyright (C) 2018 Sergey Yeriomin <yeriomin@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.util.TypedValue;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class Util {

    private static float DP_PX_RATIO = 0.0f;
    private static final Map<Integer, String> siPrefixes = new HashMap<>();
    static {
        siPrefixes.put(0, "");
        siPrefixes.put(3, "K");
        siPrefixes.put(6, "M");
        siPrefixes.put(9, "G");
        siPrefixes.put(12, "T");
    }

    static public int getColor(Context context, int attrId) {
        TypedValue outValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        boolean wasResolved = theme.resolveAttribute(attrId, outValue, true);
        if (wasResolved) {
            return outValue.resourceId == 0
                ? outValue.data
                : (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    ? context.getColor(outValue.resourceId)
                    : context.getResources().getColor(outValue.resourceId)
                )
            ;
        } else {
            return Color.BLACK;
        }
    }

    static public Map<String, String> sort(Map<String, String> unsorted) {

        class CaseInsensitiveComparator implements Comparator<String> {

            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        }

        Map<String, String> sortedByKey = new TreeMap<>(new CaseInsensitiveComparator());
        sortedByKey.putAll(swapKeysValues(unsorted));
        Map<String, String> sorted = new LinkedHashMap<>();
        for (String value: sortedByKey.keySet()) {
            sorted.put(sortedByKey.get(value), value);
        }
        return sorted;
    }

    static public <K,V> Map<V,K> swapKeysValues(Map<K,V> map) {
        Map<V,K> swapped = new HashMap<>();
        for(Map.Entry<K,V> entry : map.entrySet()) {
            swapped.put(entry.getValue(), entry.getKey());
        }
        return swapped;
    }

    static public Map<String, String> addToStart(LinkedHashMap<String, String> map, String key, String value) {
        LinkedHashMap<String, String> clonedMap = (LinkedHashMap<String, String>) map.clone();
        map.clear();
        map.put(key, value);
        map.putAll(clonedMap);
        return map;
    }

    static public int parseInt(String intAsString, int defaultValue) {
        try {
            return Integer.parseInt(intAsString);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    static public void closeSilently(Closeable closeable) {
        if (null == closeable) {
            return;
        }
        try {
            closeable.close();
        } catch (IOException e) {
            // Closing silently
        }
    }

    static public String addSiPrefix(Integer integer) {
        int tempValue = integer;
        int order = 0;
        while(tempValue >= 1000.0) {
            tempValue /= 1000.0;
            order += 3;
        }
        return tempValue + siPrefixes.get(order);
    }

    static public String readableFileSize(long size) {
        if (size <= 0) {
            return "0 B";
        }
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + siPrefixes.get(3 * digitGroups) + "B";
    }

    static public int getPx(Context context, int dp) {
        if (DP_PX_RATIO == 0.0f) {
            DP_PX_RATIO = context.getResources().getDisplayMetrics().density;
        }
        return (int) (dp * DP_PX_RATIO);
    }

    static public byte[] getFileChecksum(File file) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        FileInputStream inputStream = null;
        try {
            byte[] buffer = new byte[2048];
            int bytesRead;
            inputStream = new FileInputStream(file);
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                md.update(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            return null;
        } finally {
            closeSilently(inputStream);
        }
        return md.digest();
    }

    public static byte[] base64StringToByteArray(String string) {
        return com.github.yeriomin.playstoreapi.Base64.decode(
            string,
            com.github.yeriomin.playstoreapi.Base64.URL_SAFE | com.github.yeriomin.playstoreapi.Base64.NO_PADDING
        );
    }
}
