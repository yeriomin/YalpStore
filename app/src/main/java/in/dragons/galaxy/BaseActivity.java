package in.dragons.galaxy;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;

import com.afollestad.aesthetic.AestheticActivity;
import com.percolate.caffeine.PhoneUtils;
import com.percolate.caffeine.ToastUtils;
import com.percolate.caffeine.ViewUtils;
import com.squareup.picasso.Picasso;

public abstract class BaseActivity extends AestheticActivity {

    static protected boolean logout = false;

    protected String Email, Name, Url;
    protected SharedPreferences sharedPreferences;

    public static void cascadeFinish() {
        BaseActivity.logout = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        setTheme(sharedPreferences.getBoolean("THEME", true) ? R.style.AppTheme : R.style.AppTheme_Dark);

        super.onCreate(savedInstanceState);

        logout = false;
        Email = sharedPreferences.getString(PlayStoreApiAuthenticator.PREFERENCE_EMAIL, "");
    }

    protected boolean isConnected() {
        return PhoneUtils.isNetworkAvailable(this);
    }

    protected boolean isValidEmail(String Email) {
        return !(Email.isEmpty() || isDummyEmail());
    }

    protected boolean isDummyEmail() {
        return (Email.contains("yalp.store.user"));
    }

    protected void notifyConnected(final Context context) {
        if (!isConnected())
            ToastUtils.quickToast(this, "No network").show();
    }

    protected void parseRAW(String rawData) {
        Name = rawData.substring(rawData.indexOf("<name>") + 6, rawData.indexOf("</name>"));
        Url = rawData.substring(rawData.indexOf("<gphoto:thumbnail>") + 18, rawData.lastIndexOf("</gphoto:thumbnail>"));

        PreferenceManager.getDefaultSharedPreferences(this).edit().putString("GOOGLE_NAME", Name).apply();
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString("GOOGLE_URL", Url).apply();

        setNavHeaderInfo((NavigationView) findViewById(R.id.nav_view), Name, Url);
    }

    protected void setNavHeaderInfo(NavigationView navigationView, String Name, String URL) {
        ViewUtils.setText(this, R.id.usr_name, Name);
        ViewUtils.setText(this, R.id.usr_email, Email);

        Picasso.with(this)
                .load(URL)
                .placeholder(R.drawable.ic_user_placeholder)
                .transform(new CircleTransform())
                .into((ImageView) ViewUtils.findViewById(this, R.id.usr_img));
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    protected void addQueryTextListener(MenuItem searchItem) {
        SearchView searchView = (SearchView) searchItem.getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (null != searchManager) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setQueryHint(getString(R.string.search_title));
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent i = new Intent(BaseActivity.this, SearchActivity.class);
                i.setAction(Intent.ACTION_SEARCH);
                i.putExtra(SearchManager.QUERY, query);
                startActivity(i);
                return false;
            }
        });
    }

    AlertDialog showLogOutDialog() {
        return new AlertDialog.Builder(this)
                .setMessage(R.string.dialog_message_logout)
                .setTitle(R.string.dialog_title_logout)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new PlayStoreApiAuthenticator(getApplicationContext()).logout();
                        dialogInterface.dismiss();
                        finishAll();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }


    AlertDialog showFallbackSearchDialog() {
        final EditText textView = new EditText(this);
        return new AlertDialog.Builder(this)
                .setView(textView)
                .setPositiveButton(android.R.string.search_go, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(getApplicationContext(), SearchActivity.class);
                        i.setAction(Intent.ACTION_SEARCH);
                        i.putExtra(SearchManager.QUERY, textView.getText().toString());
                        startActivity(i);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    protected void finishAll() {
        logout = true;
        finish();
    }

}
