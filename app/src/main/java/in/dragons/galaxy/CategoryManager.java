package in.dragons.galaxy;

import android.content.Context;
import android.preference.PreferenceManager;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class CategoryManager {

    public static final String TOP = "0_CATEGORY_TOP";

    private Context context;
    private SharedPreferencesTranslator translator;

    public CategoryManager(Context context) {
        this.context = context;
        translator = new SharedPreferencesTranslator(PreferenceManager.getDefaultSharedPreferences(context));
    }

    public String getCategoryName(String categoryId) {
        if (null == categoryId) {
            return "";
        }
        if (categoryId.equals(TOP)) {
            return context.getString(R.string.search_filter);
        }
        return translator.getString(categoryId);
    }

    public void save(String parent, Map<String, String> categories) {
        Util.putStringSet(context, parent, categories.keySet());
        for (String categoryId : categories.keySet()) {
            translator.putString(categoryId, categories.get(categoryId));
        }
    }

    public boolean fits(String appCategoryId, String chosenCategoryId) {
        return null == chosenCategoryId
                || chosenCategoryId.equals(TOP)
                || appCategoryId.equals(chosenCategoryId)
                || Util.getStringSet(context, chosenCategoryId).contains(appCategoryId)
                ;
    }

    public boolean categoryListEmpty() {
        Set<String> topSet = Util.getStringSet(context, TOP);
        if (topSet.isEmpty()) {
            return true;
        }
        int size = topSet.size();
        String categoryId = topSet.toArray(new String[size])[size - 1];
        return translator.getString(categoryId).equals(categoryId);
    }

    public Map<String, String> getCategoriesFromSharedPreferences() {
        Map<String, String> categories = new TreeMap<>();
        Set<String> topSet = Util.getStringSet(context, TOP);
        for (String topCategoryId : topSet) {
            categories.put(topCategoryId, translator.getString(topCategoryId));
        }
        return Util.sort(categories);
    }
}
