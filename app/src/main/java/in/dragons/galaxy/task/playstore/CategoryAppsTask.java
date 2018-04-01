package in.dragons.galaxy.task.playstore;

import com.github.yeriomin.playstoreapi.CategoryAppsIterator;
import com.github.yeriomin.playstoreapi.GooglePlayAPI;

import java.io.IOException;

import in.dragons.galaxy.AppListIterator;
import in.dragons.galaxy.PlayStoreApiAuthenticator;

public class CategoryAppsTask extends EndlessScrollTask implements CloneableTask {

    private String categoryId;
    private GooglePlayAPI.SUBCATEGORY subCategory;

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public void setSubCategory(GooglePlayAPI.SUBCATEGORY subCategory) {
        this.subCategory = subCategory;
    }

    public CategoryAppsTask(AppListIterator iterator) {
        super(iterator);
    }

    @Override
    public CloneableTask clone() {
        CategoryAppsTask task = new CategoryAppsTask(iterator);
        task.setFilter(filter);
        task.setCategoryId(categoryId);
        task.setErrorView(errorView);
        task.setContext(context);
        task.setProgressIndicator(progressIndicator);
        return task;
    }

    @Override
    protected AppListIterator initIterator() throws IOException {
        return new AppListIterator(new CategoryAppsIterator(
                new PlayStoreApiAuthenticator(context).getApi(),
                categoryId,
                subCategory));
    }
}
