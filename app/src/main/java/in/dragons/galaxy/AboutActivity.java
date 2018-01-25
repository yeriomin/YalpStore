package in.dragons.galaxy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.ClipboardManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class AboutActivity extends YalpStoreActivity implements NavigationView.OnNavigationItemSelectedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity_layout);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ((TextView) findViewById(R.id.version)).setText(BuildConfig.VERSION_NAME);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        ((TextView) findViewById(R.id.user_email)).setText(sharedPreferences.getString(PlayStoreApiAuthenticator.PREFERENCE_EMAIL, ""));
        TextView gsfIdView = (TextView) findViewById(R.id.gsf_id);
        gsfIdView.setText(sharedPreferences.getString(PlayStoreApiAuthenticator.PREFERENCE_GSF_ID, ""));
        gsfIdView.setOnClickListener(new CopyToClipboardListener());
    }

    private class CopyToClipboardListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE)).setText(((TextView) v).getText());
            Toast.makeText(v.getContext().getApplicationContext(), R.string.about_copied_to_clipboard, Toast.LENGTH_SHORT).show();
        }
    }

    public class UriOpeningListener extends CopyToClipboardListener {
        @Override
        public void onClick(View v) {
            super.onClick(v);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getUri(v)));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }

        protected String getUri(View v) {
            return (String) ((TextView) v).getText();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, PreferenceActivity.class));
                break;
            case R.id.action_logout:
                showLogOutDialog();
                break;
            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
