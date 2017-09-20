package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Util {

    private static final String DELIMITER = ",";
    private static final Map<Integer, String> siPrefixes = new HashMap<>();
    static {
        siPrefixes.put(0, "");
        siPrefixes.put(3, "K");
        siPrefixes.put(6, "M");
        siPrefixes.put(9, "G");
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

    static public Set<String> getStringSet(Context context, String key) {
        return new HashSet<>(Arrays.asList(TextUtils.split(
            PreferenceManager.getDefaultSharedPreferences(context).getString(key, ""),
            DELIMITER
        )));
    }

    static public void putStringSet(Context context, String key, Set<String> set) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key, TextUtils.join(DELIMITER, set)).commit();
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
}
