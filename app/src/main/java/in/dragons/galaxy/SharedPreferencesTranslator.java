package in.dragons.galaxy;

import android.content.SharedPreferences;

import java.util.Locale;

public class SharedPreferencesTranslator {

    private static final String PREFIX = "translation";
    private SharedPreferences prefs;

    public SharedPreferencesTranslator(SharedPreferences prefs) {
        this.prefs = prefs;
    }

    public String getString(String id, Object... params) {
        return String.format(prefs.getString(getFullId(id), id), params);
    }

    public void putString(String id, String value) {
        prefs.edit().putString(getFullId(id), value).apply();
    }

    static private String getFullId(String partId) {
        return PREFIX + "_" + Locale.getDefault().getLanguage() + "_" + partId;
    }
}
