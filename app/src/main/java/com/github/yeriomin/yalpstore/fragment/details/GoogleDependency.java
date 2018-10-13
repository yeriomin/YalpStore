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

package com.github.yeriomin.yalpstore.fragment.details;

import android.text.TextUtils;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.DetailsActivity;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.SharedPreferencesTranslator;
import com.github.yeriomin.yalpstore.fragment.Abstract;
import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.task.playstore.DependencyTranslationTask;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GoogleDependency extends Abstract {

    private SharedPreferencesTranslator translator;

    public GoogleDependency(DetailsActivity activity, App app) {
        super(activity, app);
        translator = new SharedPreferencesTranslator(activity);
    }

    @Override
    public void draw() {
        Set<String> untranslated = new HashSet<>();
        Set<String> translated = getTranslatedDeps(app);
        for (String dependency: translated) {
            if (app.getDependencies().contains(dependency)) {
                untranslated.add(dependency);
            }
        }
        drawDeps(translated);
        if (untranslated.size() > 0) {
            getTranslations(untranslated);
        }
    }

    private void drawDeps(Set<String> dependencies) {
        String depsList = app.getDependencies().isEmpty()
            ? activity.getString(R.string.details_no_dependencies)
            : TextUtils.join(", ", dependencies)
        ;
        ((TextView) activity.findViewById(R.id.google_dependencies)).setText(activity.getString(R.string.details_depends_on, depsList));
    }

    private Set<String> getTranslatedDeps(App app) {
        Set<String> translated = new HashSet<>();
        for (String dependency: app.getDependencies()) {
            translated.add(translator.getString(dependency));
        }
        return translated;
    }

    private void getTranslations(Set<String> untranslated) {
        DependencyTranslationTask task = new DependencyTranslationTask() {

            @Override
            protected void onPostExecute(List<App> apps) {
                super.onPostExecute(apps);
                if (!success()) {
                    return;
                }
                for (String packageName: translated.keySet()) {
                    translator.putString(packageName, translated.get(packageName));
                }
                drawDeps(getTranslatedDeps(app));
            }
        };
        task.setContext(activity);
        task.execute(untranslated.toArray(new String[untranslated.size()]));
    }
}
