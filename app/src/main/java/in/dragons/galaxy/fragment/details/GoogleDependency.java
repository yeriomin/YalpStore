package in.dragons.galaxy.fragment.details;

import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.TextView;

import in.dragons.galaxy.DetailsActivity;
import in.dragons.galaxy.R;
import in.dragons.galaxy.SharedPreferencesTranslator;
import in.dragons.galaxy.model.App;
import in.dragons.galaxy.task.playstore.DependencyTranslationTask;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GoogleDependency extends Abstract {

    private SharedPreferencesTranslator translator;

    public GoogleDependency(DetailsActivity activity, App app) {
        super(activity, app);
        translator = new SharedPreferencesTranslator(PreferenceManager.getDefaultSharedPreferences(activity));
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
