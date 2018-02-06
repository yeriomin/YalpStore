package in.dragons.galaxy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import in.dragons.galaxy.task.playstore.CategoryListTask;
import in.dragons.galaxy.task.playstore.CategoryTask;


public class CategoryListActivity extends GalaxyActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        setTheme(sharedPreferences.getBoolean("THEME", true)?R.style.AppTheme:R.style.AppTheme_Dark);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categories_activity_layout);
        super.onCreateDrawer(savedInstanceState);
        setTitle(getString(R.string.action_categories));

        CategoryManager manager = new CategoryManager(this);
        getTask(manager).execute();
    }

    private CategoryTask getTask(CategoryManager manager) {
        CategoryListTask task = new CategoryListTask();
        task.setContext(this);
        task.setManager(manager);
        task.setErrorView((TextView) findViewById(R.id.empty));
        task.setProgressIndicator(findViewById(R.id.progress));
        return task;
    }
}
