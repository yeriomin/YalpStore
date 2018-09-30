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

package com.github.yeriomin.yalpstore.view;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.model.ImageSource;
import com.github.yeriomin.yalpstore.task.LoadImageTask;

import java.util.WeakHashMap;

public abstract class ListItem {

    static private WeakHashMap<Integer, LoadImageTask> tasks = new WeakHashMap<>();

    protected App app;
    protected View view;

    public App getApp() {
        return app;
    }

    public View getView() {
        return view;
    }

    public void setApp(App app) {
        this.app = app;
    }

    public void setView(View view) {
        this.view = view;
    }

    abstract public void draw();

    protected void drawIcon(ImageView imageView, String packageName, ImageSource imageSource) {
        String tag = (String) imageView.getTag();
        if (!TextUtils.isEmpty(tag) && tag.equals(packageName)) {
            return;
        }
        imageView.setTag(packageName);
        LoadImageTask task = new LoadImageTask(imageView);
        task.setImageSource(imageSource);
        LoadImageTask previousTask = tasks.get(imageView.hashCode());
        if (null != previousTask) {
            previousTask.cancel(true);
        }
        tasks.put(imageView.hashCode(), task);
        task.executeOnExecutorIfPossible();
    }
}
