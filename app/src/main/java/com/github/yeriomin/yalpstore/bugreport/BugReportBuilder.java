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

package com.github.yeriomin.yalpstore.bugreport;

import android.content.Context;
import android.util.Log;

import com.github.yeriomin.yalpstore.Util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class BugReportBuilder {

    protected Context context;
    protected File file;
    protected String content;

    public File getFile() {
        return file;
    }

    public BugReportBuilder setFileName(String fileName) {
        file = new File(context.getCacheDir(), fileName);
        return this;
    }

    public BugReportBuilder setContent(String content) {
        this.content = content;
        return this;
    }

    public BugReportBuilder(Context context) {
        this.context = context;
    }

    public BugReportBuilder build() {
        if (null == file) {
            Log.e(getClass().getSimpleName(), "No file specified");
            return this;
        }
        file.delete();
        write(content);
        return this;
    }

    protected void write(String content) {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file));
            bw.write(content);
        } catch (IOException e) {
            Log.e(getClass().getSimpleName(), "Could not write to temp file: " + e.getMessage());
        } finally {
            Util.closeSilently(bw);
        }
    }
}
