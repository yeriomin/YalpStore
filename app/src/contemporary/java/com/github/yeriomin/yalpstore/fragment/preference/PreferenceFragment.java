package com.github.yeriomin.yalpstore.fragment.preference;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.github.yeriomin.yalpstore.PreferenceActivity;
import com.github.yeriomin.yalpstore.R;

public class PreferenceFragment extends android.preference.PreferenceFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        new AllPreferences((PreferenceActivity) getActivity()).draw();
    }
}
