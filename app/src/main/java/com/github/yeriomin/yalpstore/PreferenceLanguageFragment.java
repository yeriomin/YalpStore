package com.github.yeriomin.yalpstore;

import android.preference.ListPreference;
import android.preference.Preference;
import android.text.TextUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class PreferenceLanguageFragment extends PreferenceFragment {

    private ListPreference languagesPreference;

    public PreferenceLanguageFragment(PreferenceActivity activity) {
        super(activity);
    }

    public void setLanguagesPreference(ListPreference languagesPreference) {
        this.languagesPreference = languagesPreference;
    }

    @Override
    public void draw() {
        final Map<String, String> localeList = getLanguages();
        int count = localeList.size();
        languagesPreference.setEntries(localeList.values().toArray(new CharSequence[count]));
        languagesPreference.setEntryValues(localeList.keySet().toArray(new CharSequence[count]));
        Preference.OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (TextUtils.isEmpty((CharSequence) newValue)) {
                    preference.setSummary(activity.getString(R.string.pref_requested_language_default));
                    return true;
                }
                preference.setSummary(localeList.get(newValue));
                try {
                    new PlayStoreApiAuthenticator(activity).getApi().setLocale(new Locale((String) newValue));
                } catch (IOException e) {
                    // Should be impossible to get to preferences with incorrect credentials
                }
                return true;
            }
        };
        languagesPreference.setOnPreferenceChangeListener(listener);
        listener.onPreferenceChange(languagesPreference, languagesPreference.getValue());
    }

    private Map<String, String> getLanguages() {
        Map<String, String> languages = new HashMap<>();
        for (Locale locale: Locale.getAvailableLocales()) {
            String displayName = locale.getDisplayName();
            displayName = displayName.substring(0, 1).toUpperCase() + displayName.substring(1);
            languages.put(locale.toString(), displayName);
        }
        languages = Util.sort(languages);
        Util.addToStart(
            (LinkedHashMap<String, String>) languages,
            "",
            activity.getString(R.string.pref_requested_language_default)
        );
        return languages;
    }
}
