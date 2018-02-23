package in.dragons.galaxy;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class AboutActivity extends GalaxyActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.app_abt_inc, contentFrameLayout);

        drawVersion();
        drawActions();
        drawDevCard(R.string.dev1_imgURL, (ImageView) findViewById(R.id.dev1_avatar));
        drawDevCard(R.string.dev2_imgURL, (ImageView) findViewById(R.id.dev2_avatar));
        drawList(getResources().getStringArray(R.array.contributors), ((TextView) findViewById(R.id.contributors)));
        drawList(getResources().getStringArray(R.array.opensource), ((TextView) findViewById(R.id.opensource)));
    }

    private void drawVersion() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            ((TextView) findViewById(R.id.app_version)).setText(packageInfo.versionName + "." + packageInfo.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void drawActions() {
        final Intent browserIntent = new Intent(Intent.ACTION_VIEW);
        ((TextView) findViewById(R.id.github)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                browserIntent.setData(Uri.parse(getResources().getString(R.string.linkGit)));
                startActivity(browserIntent);
            }
        });
        ((TextView) findViewById(R.id.xda)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                browserIntent.setData(Uri.parse(getResources().getString(R.string.linkXDA)));
                startActivity(browserIntent);
            }
        });
        ((TextView) findViewById(R.id.telegram)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                browserIntent.setData(Uri.parse(getResources().getString(R.string.linkTelegram)));
                startActivity(browserIntent);
            }
        });
    }

    private void drawDevCard(int URL, ImageView imageView) {
        Picasso.with(this)
                .load(getResources().getString(URL))
                .placeholder(R.drawable.ic_user_placeholder)
                .transform(new CircleTransform())
                .into(imageView);
    }

    private void drawList(String[] List, TextView tv) {
        StringBuilder builder = new StringBuilder();
        for (String s : List) {
            builder.append("◉  ");
            builder.append(s);
            builder.append("\n");
        }
        (tv).setText(builder.toString().trim());
    }
}
