package in.dragons.galaxy.task.playstore;

import com.github.yeriomin.playstoreapi.GooglePlayAPI;
import com.github.yeriomin.playstoreapi.UrlIterator;

import java.io.IOException;

import in.dragons.galaxy.AppListIterator;
import in.dragons.galaxy.PlayStoreApiAuthenticator;

public class ClusterTask extends EndlessScrollTask implements CloneableTask {

    private String clusterUrl;

    public ClusterTask(AppListIterator iterator) {
        super(iterator);
    }

    public void setClusterUrl(String clusterUrl) {
        this.clusterUrl = clusterUrl;
    }

    @Override
    public CloneableTask clone() {
        ClusterTask task = new ClusterTask(iterator);
        task.setClusterUrl(clusterUrl);
        task.setFilter(filter);
        task.setErrorView(errorView);
        task.setContext(context);
        task.setProgressIndicator(progressIndicator);
        return task;
    }

    @Override
    protected AppListIterator initIterator() throws IOException {
        return new AppListIterator(new UrlIterator(new PlayStoreApiAuthenticator(context).getApi(), clusterUrl));
    }

    @Override
    public void setSubCategory(GooglePlayAPI.SUBCATEGORY subCategory) {

    }
}
