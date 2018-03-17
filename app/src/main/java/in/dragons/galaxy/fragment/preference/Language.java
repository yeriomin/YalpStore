package in.dragons.galaxy.fragment.preference;

import android.preference.Preference;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import in.dragons.galaxy.OnListPreferenceChangeListener;
import in.dragons.galaxy.PlayStoreApiAuthenticator;
import in.dragons.galaxy.fragment.PreferenceFragment;
import in.dragons.galaxy.R;
import in.dragons.galaxy.Util;

public class Language extends List {

    public Language(PreferenceFragment activity) {
        super(activity);
    }

    @Override
    protected OnListPreferenceChangeListener getOnListPreferenceChangeListener() {
        OnListPreferenceChangeListener listener = new OnListPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean result = super.onPreferenceChange(preference, newValue);
                try {
                    new PlayStoreApiAuthenticator(activity.getActivity()).getApi().setLocale(new Locale((String) newValue));
                } catch (IOException e) {
                    // Should be impossible to get to preferences with incorrect credentials
                }
                return result;
            }
        };
        listener.setDefaultLabel(activity.getString(R.string.pref_requested_language_default));
        return listener;
    }

    @Override
    protected Map<String, String> getKeyValueMap() {
        Map<String, String> languages = new HashMap<>();
        for (Locale locale : Locale.getAvailableLocales()) {
            String displayName = locale.getDisplayName();
            displayName = displayName.substring(0, 1).toUpperCase(Locale.getDefault()) + displayName.substring(1);
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
