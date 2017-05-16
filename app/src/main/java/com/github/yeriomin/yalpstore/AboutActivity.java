package com.github.yeriomin.yalpstore;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.ClipboardManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class AboutActivity extends YalpStoreActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity_layout);
        ((TextView) findViewById(R.id.version)).setText(BuildConfig.VERSION_NAME);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String email = sharedPreferences.getString(PreferenceActivity.PREFERENCE_EMAIL, "");
        String gsfId = sharedPreferences.getString(PreferenceActivity.PREFERENCE_GSF_ID, "");
        ((TextView) findViewById(R.id.user_email)).setText(email);
        TextView gsfIdView = (TextView) findViewById(R.id.gsf_id);
        gsfIdView.setText(gsfId);
        gsfIdView.setOnClickListener(new CopyToClipboardListener());
        findViewById(R.id.developer_email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CrashLetterActivity.send(AboutActivity.this, null);
            }
        });
        findViewById(R.id.website).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse((String) ((TextView) v).getText()));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
        if (hasPermission()) {
            checkForUpdate();
        }
    }

    private void checkForUpdate() {
        SelfUpdateChecker checker = new SelfUpdateChecker(this);
        checker.setButton((TextView) findViewById(R.id.update));
        checker.execute();
    }

    private boolean hasPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            return checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    private class CopyToClipboardListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE)).setText(((TextView) v).getText());
            Toast.makeText(v.getContext(), R.string.about_copied_to_clipboard, Toast.LENGTH_SHORT).show();
        }
    }
}
