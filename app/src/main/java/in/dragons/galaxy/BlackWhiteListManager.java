package in.dragons.galaxy;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BlackWhiteListManager {

    static private final String DELIMITER = ",";

    private SharedPreferences preferences;
    private Set<String> blackWhiteSet;

    public BlackWhiteListManager(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        blackWhiteSet = new HashSet<>(Arrays.asList(TextUtils.split(
            preferences.getString(PreferenceActivity.PREFERENCE_UPDATE_LIST, ""),
            DELIMITER
        )));
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
        return preferences.getString(PreferenceActivity.PREFERENCE_UPDATE_LIST_WHITE_OR_BLACK, PreferenceActivity.LIST_BLACK).equals(PreferenceActivity.LIST_BLACK);
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
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(
            PreferenceActivity.PREFERENCE_UPDATE_LIST,
            TextUtils.join(DELIMITER, blackWhiteSet)
        );
        editor.commit();
    }
}
