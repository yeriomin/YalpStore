package in.dragons.galaxy;

import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import java.util.List;

import in.dragons.galaxy.model.App;
import in.dragons.galaxy.task.playstore.EndlessScrollTask;
import in.dragons.galaxy.view.ListItem;
import in.dragons.galaxy.view.ProgressIndicator;
import in.dragons.galaxy.view.SearchResultAppBadge;

abstract public class EndlessScrollActivity extends AppListActivity {

    protected AppListIterator iterator;

    abstract protected EndlessScrollTask getTask();

    public void setIterator(AppListIterator iterator) {
        this.iterator = iterator;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onNewIntent(getIntent());
        getListView().setOnScrollListener(new ScrollEdgeListener() {

            @Override
            protected void loadMore() {
                loadApps();
            }
        });
    }

    @Override
    protected ListItem getListItem(App app) {
        SearchResultAppBadge appBadge = new SearchResultAppBadge();
        appBadge.setApp(app);
        return appBadge;
    }

    @Override
    public void addApps(List<App> appsToAdd) {
        AppListAdapter adapter = (AppListAdapter) getListView().getAdapter();
        if (!adapter.isEmpty()) {
            ListItem last = adapter.getItem(adapter.getCount() - 1);
            if (last instanceof ProgressIndicator) {
                adapter.remove(last);
            }
        }
        super.addApps(appsToAdd, false);
        if (!appsToAdd.isEmpty()) {
            adapter.add(new ProgressIndicator());
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void clearApps() {
        super.clearApps();
        iterator = null;
    }

    protected EndlessScrollTask prepareTask(EndlessScrollTask task) {
        task.setContext(this);
        task.setErrorView((TextView) getListView().getEmptyView());
        if (listItems.isEmpty())
            task.setProgressIndicator(findViewById(R.id.progress));
        return task;
    }

    @Override
    public void loadApps() {
        prepareTask(getTask()).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.findItem(R.id.action_filter).setVisible(true);
        menu.findItem(R.id.filter_apps_with_ads).setVisible(true);
        menu.findItem(R.id.filter_paid_apps).setVisible(true);
        menu.findItem(R.id.filter_gsf_dependent_apps).setVisible(true);
        menu.findItem(R.id.filter_rating).setVisible(true);
        menu.findItem(R.id.filter_downloads).setVisible(true);
        return result;
    }
}
