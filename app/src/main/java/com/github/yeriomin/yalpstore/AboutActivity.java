/*
 * Yalp Store
 * Copyright (C) 2018 Sergey Yeriomin <yeriomin@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.yeriomin.yalpstore.bugreport.BugReportSenderEmail;

public class AboutActivity extends YalpStoreActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity_layout);
        ((TextView) findViewById(R.id.version)).setText(BuildConfig.VERSION_NAME);
        ((TextView) findViewById(R.id.user_email)).setText(YalpStoreApplication.user.getEmail());
        TextView gsfIdView = (TextView) findViewById(R.id.gsf_id);
        gsfIdView.setText(YalpStoreApplication.user.getGsfId());
        gsfIdView.setOnClickListener(new CopyToClipboardListener());
        findViewById(R.id.developer_email).setOnClickListener(new CopyToClipboardListener() {
            @Override
            public void onClick(View v) {
                super.onClick(v);
                new BugReportSenderEmail(getApplicationContext()).send();
            }
        });
        findViewById(R.id.website).setOnClickListener(new UriOpeningListener());
        findViewById(R.id.librepay).setOnClickListener(new UriOpeningListener());
        findViewById(R.id.bitcoin).setOnClickListener(new UriOpeningListener() {
            @Override
            protected String getUri(View v) {
                return "bitcoin:" + super.getUri(v) + "?label=YalpStore";
            }
        });
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
}
