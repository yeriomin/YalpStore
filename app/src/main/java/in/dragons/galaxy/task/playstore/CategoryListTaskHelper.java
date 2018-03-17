package in.dragons.galaxy.task.playstore;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.percolate.caffeine.ViewUtils;

import java.util.Map;

import in.dragons.galaxy.adapters.AllCategoriesAdapter;
import in.dragons.galaxy.activities.GalaxyActivity;
import in.dragons.galaxy.R;

public class CategoryListTaskHelper extends CategoryTask implements CloneableTask {

    @Override
    public CloneableTask clone() {
        CategoryListTaskHelper task = new CategoryListTaskHelper();
        task.setManager(manager);
        task.setErrorView(errorView);
        task.setContext(context);
        task.setProgressIndicator(progressIndicator);
        return task;
    }
}
