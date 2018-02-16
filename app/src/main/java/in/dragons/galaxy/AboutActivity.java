package in.dragons.galaxy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class AboutActivity extends GalaxyActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        setTheme(sharedPreferences.getBoolean("THEME", true) ? R.style.AppTheme : R.style.AppTheme_Dark);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity_layout);
        super.onCreateDrawer(savedInstanceState);
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            ((TextView) findViewById(R.id.app_version)).setText("v " + packageInfo.versionName + "." + packageInfo.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        drawActions();
        drawDevs();
        drawContributors();
        drawOpenSource();
    }

    private void drawActions() {
        ((TextView) findViewById(R.id.github)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse("https://github.com/whyorean/Galaxy"));
                startActivity(browserIntent);
            }
        });
        ((TextView) findViewById(R.id.xda)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse("https://forum.xda-developers.com/android/apps-games/galaxy-playstore-alternative-t3739733"));
                startActivity(browserIntent);
            }
        });
        ((TextView) findViewById(R.id.telegram)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse("https://t.me/GalaxyOfficial"));
                startActivity(browserIntent);
            }
        });
    }

    private void drawDevs() {
        Picasso.with(this)
                .load("https://avatars2.githubusercontent.com/u/21051221")
                .placeholder(R.drawable.ic_user_placeholder)
                .transform(new CircleTransform())
                .into(((ImageView) findViewById(R.id.dev1_avatar)));
        Picasso.with(this)
                .load("https://avatars0.githubusercontent.com/u/554737")
                .placeholder(R.drawable.ic_user_placeholder)
                .transform(new CircleTransform())
                .into(((ImageView) findViewById(R.id.dev2_avatar)));
    }

    private void drawContributors() {
        StringBuilder builder = new StringBuilder();
        for (String s : getResources().getStringArray(R.array.contributors)) {
            builder.append("◉  ");
            builder.append(s);
            builder.append("\n");
        }
        ((TextView) findViewById(R.id.contributors)).setText(builder.toString().trim());
    }

    private void drawOpenSource() {
        StringBuilder builder = new StringBuilder();
        for (String s : getResources().getStringArray(R.array.opensource)) {
            builder.append("◉  ");
            builder.append(s);
            builder.append("\n");
        }
        ((TextView) findViewById(R.id.opensource)).setText(builder.toString().trim());
    }
}
