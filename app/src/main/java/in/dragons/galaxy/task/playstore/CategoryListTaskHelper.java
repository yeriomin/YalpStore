package in.dragons.galaxy.task.playstore;

import android.net.Uri;
import android.text.TextUtils;

import com.dragons.aurora.playstoreapiv2.BrowseLink;
import com.dragons.aurora.playstoreapiv2.BrowseResponse;
import com.dragons.aurora.playstoreapiv2.GooglePlayAPI;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import in.dragons.galaxy.CategoryManager;

public class CategoryListTaskHelper extends ForegroundUpdatableAppsTaskHelper {

    protected boolean getResult(GooglePlayAPI api, CategoryManager manager) throws IOException {
        Map<String, String> topCategories = buildCategoryMap(api.categories());
        manager = new CategoryManager(this.getActivity());
        manager.save(CategoryManager.TOP, topCategories);

        for (String categoryId : topCategories.keySet()) {
            manager.save(categoryId, buildCategoryMap(api.categories(categoryId)));
        }

        return true;
    }

    private Map<String, String> buildCategoryMap(BrowseResponse response) {
        Map<String, String> categories = new HashMap<>();
        for (BrowseLink category : response.getCategoryContainer().getCategoryList()) {
            String categoryId = Uri.parse(category.getDataUrl()).getQueryParameter("cat");
            if (TextUtils.isEmpty(categoryId)) {
                continue;
            }
            categories.put(categoryId, category.getName());
        }
        return categories;
    }
}