package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Set;

public class BlackWhiteListManager {

    private SharedPreferences preferences;
    private Set<String> blackWhiteSet;

    public BlackWhiteListManager(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        blackWhiteSet = PreferenceUtil.getStringSet(context, PreferenceUtil.PREFERENCE_UPDATE_LIST);
        if (blackWhiteSet.size() == 1 && blackWhiteSet.contains("")) {
            blackWhiteSet.clear();
        }
    }

    public boolean add(String s) {
        boolean result = blackWhiteSet.add(s);
        save();
        return result;
    }

    public boolean set(Set<String> s) {
        blackWhiteSet = s;
        if (blackWhiteSet.size() == 1 && blackWhiteSet.contains("")) {
            blackWhiteSet.clear();
        }
        save();
        return true;
    }

    public boolean isBlack() {
        return preferences.getString(PreferenceUtil.PREFERENCE_UPDATE_LIST_WHITE_OR_BLACK, PreferenceUtil.LIST_BLACK).equals(PreferenceUtil.LIST_BLACK);
    }

    public boolean isUpdatable(String packageName) {
        boolean isContained = contains(packageName);
        boolean isBlackList = isBlack();
        return (isBlackList && !isContained) || (!isBlackList && isContained);
    }

    public Set<String> get() {
        return blackWhiteSet;
    }

    public boolean contains(String s) {
        return blackWhiteSet.contains(s);
    }

    public boolean remove(String s) {
        boolean result = blackWhiteSet.remove(s);
        save();
        return result;
    }

    private void save() {
        PreferenceUtil.putStringSet(preferences, PreferenceUtil.PREFERENCE_UPDATE_LIST, blackWhiteSet);
    }
}
