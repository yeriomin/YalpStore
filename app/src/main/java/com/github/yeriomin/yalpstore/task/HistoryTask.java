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

package com.github.yeriomin.yalpstore.task;

import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.ContextUtil;
import com.github.yeriomin.yalpstore.HistoryActivity;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.SqliteHelper;
import com.github.yeriomin.yalpstore.YalpStoreApplication;
import com.github.yeriomin.yalpstore.model.Event;
import com.github.yeriomin.yalpstore.model.EventDao;
import com.github.yeriomin.yalpstore.view.HistoryItem;
import com.github.yeriomin.yalpstore.view.ListItem;

import java.util.ArrayList;
import java.util.List;

public class HistoryTask extends TaskWithProgress<List<Event>> {

    private String packageName;

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    @Override
    protected List<Event> doInBackground(String... strings) {
        SQLiteDatabase db = new SqliteHelper(context).getReadableDatabase();
        try {
            return new EventDao(db).getByPackageName(packageName);
        } finally {
            db.close();
        }
    }

    @Override
    protected void onPostExecute(List<Event> events) {
        super.onPostExecute(events);
        if (!ContextUtil.isAlive(context)) {
            return;
        }
        List<ListItem> listItems = new ArrayList<>();
        for (Event event: events) {
            HistoryItem listItem = new HistoryItem();
            listItem.setEvent(event);
            if (!TextUtils.isEmpty(event.getPackageName())) {
                listItem.setApp(YalpStoreApplication.installedPackages.get(event.getPackageName()));
            }
            listItems.add(listItem);
        }
        ((HistoryActivity) context).addItems(listItems);
        if (listItems.isEmpty()) {
            ((TextView) ((HistoryActivity) context).findViewById(R.id.empty)).setText(R.string.list_empty_history);
        }
    }
}
