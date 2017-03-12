package com.github.yeriomin.yalpstore;

import com.github.yeriomin.yalpstore.model.App;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DependencyTranslationTask extends GoogleApiAsyncTask {

    private Set<String> packageNames;

    protected Map<String, String> translated = new HashMap<>();

    public void setPackageNames(Set<String> packageNames) {
        this.packageNames = packageNames;
    }

    @Override
    protected Throwable doInBackground(String... params) {
        PlayStoreApiWrapper wrapper = new PlayStoreApiWrapper(this.context);
        try {
            for (App app: wrapper.getDetails(new ArrayList<>(packageNames))) {
                translated.put(app.getPackageName(), app.getDisplayName());
            }
        } catch (Throwable e) {
            return e;
        }
        return null;
    }
}
