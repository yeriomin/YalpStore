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

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.task.playstore.PurchaseCheckTask;

import java.util.Timer;
import java.util.TimerTask;

public class ManualDownloadActivity extends DetailsActivity {

    private int latestVersionCode;

    @Override
    protected void onNewIntent(Intent intent) {
        if (null == DetailsActivity.app) {
            Log.e(getClass().getSimpleName(), "No app stored");
            finish();
            return;
        }
        latestVersionCode = DetailsActivity.app.getVersionCode();
        draw(DetailsActivity.app);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DetailsActivity.app.setVersionCode(latestVersionCode);
    }

    private void draw(App app) {
        setTitle(app.getDisplayName());
        setContentView(R.layout.manual_download_activity_layout);
        if (app.getOfferType() == 0) {
            app.setOfferType(1);
        }
        ((TextView) findViewById(R.id.compatibility)).setText(
            app.getVersionCode() > 0
                ? R.string.manual_download_compatible
                : R.string.manual_download_incompatible
        );
        if (app.getVersionCode() > 0) {
            ((EditText) findViewById(R.id.version_code)).setHint(String.valueOf(latestVersionCode));
        }
        ManualDownloadTextWatcher textWatcher = new ManualDownloadTextWatcher(app, this);
        String versionCode = Integer.toString(app.getVersionCode());
        textWatcher.onTextChanged(versionCode, 0, 0, versionCode.length());
        ((EditText) findViewById(R.id.version_code)).addTextChangedListener(textWatcher);
        redrawButtons();
    }

    @Override
    public void redrawDetails(App app) {
        unregisterInstallReceiver();
        redrawButtons();
    }

    static private class ManualDownloadTextWatcher implements TextWatcher {

        static private final int TIMEOUT = 1000;

        private final App app;
        private final ManualDownloadActivity activity;
        private Timer timer;

        public ManualDownloadTextWatcher(App app, ManualDownloadActivity activity) {
            this.app = app;
            this.activity = activity;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                app.setVersionCode(Integer.parseInt(s.toString()));
                Button installButton = activity.findViewById(R.id.install);
                if (null != installButton) {
                    installButton.setVisibility(View.GONE);
                }
                Button downloadButton = activity.findViewById(R.id.download);
                if (null != downloadButton) {
                    downloadButton.setText(R.string.details_download_checking);
                    downloadButton.setEnabled(false);
                    downloadButton.setVisibility(View.VISIBLE);
                }
                restartTimer();
            } catch (NumberFormatException e) {
                Log.w(getClass().getSimpleName(), s.toString() + " is not a number");
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }

        private void restartTimer() {
            if (null != timer) {
                timer.cancel();
            }
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    getTask(timer).execute();
                }
            }, TIMEOUT);
        }

        private PurchaseCheckTask getTask(Timer timer) {
            PurchaseCheckTask task = new PurchaseCheckTask();
            task.setContext(activity);
            task.setTimer(timer);
            task.setApp(app);
            return task;
        }
    }
}
