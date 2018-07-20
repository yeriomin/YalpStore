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

package com.github.yeriomin.yalpstore.task.playstore;

import android.content.pm.PackageInfo;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.github.yeriomin.playstoreapi.GooglePlayAPI;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.SqliteHelper;
import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.model.Event;
import com.github.yeriomin.yalpstore.model.EventDao;

import java.io.IOException;

public class ChangelogTask extends DetailsTask {

    private App app;
    private Event.TYPE eventType;
    private boolean success;

    public void setApp(App app) {
        this.app = app;
        setPackageName(app.getPackageName());
    }

    public void setEventType(Event.TYPE eventType, boolean success) {
        this.eventType = eventType;
        this.success = success;
    }

    @Override
    protected App getResult(GooglePlayAPI api, String... arguments) throws IOException {
        if (!Event.TYPE.REMOVAL.equals(eventType)
            && (TextUtils.isEmpty(app.getVersionName())
                || TextUtils.isEmpty(app.getInstalledVersionName())
                || TextUtils.isEmpty(app.getChanges())
            )
        ) {
            PackageInfo pi = app.getPackageInfo();
            app = super.getResult(api, arguments);
            if (null != pi) {
                app.setPackageInfo(pi);
            }
        }
        insertEvent(getEvent());
        return app;
    }

    private void insertEvent(Event event) {
        SQLiteDatabase db = new SqliteHelper(context).getWritableDatabase();
        new EventDao(db).insert(event);
        db.close();
    }

    private Event getEvent() {
        Event event = new Event();
        event.setPackageName(packageName);
        event.setType(eventType);
        switch (eventType) {
            case INSTALLATION:
                event.setMessage(context.getString(success ? R.string.details_installed : R.string.details_install_failure));
                break;
            case REMOVAL:
                event.setMessage(context.getString(R.string.uninstalled));
                break;
            case UPDATE:
                event.setMessage(
                    success
                    ? context.getString(
                        R.string.updated_from_to,
                        app.getInstalledVersionName(),
                        app.getInstalledVersionCode(),
                        app.getVersionName(),
                        app.getVersionCode()
                    )
                    : context.getString(R.string.details_install_failure)
                );
                event.setChanges(app.getChanges());
                break;
        }
        return event;
    }
}
