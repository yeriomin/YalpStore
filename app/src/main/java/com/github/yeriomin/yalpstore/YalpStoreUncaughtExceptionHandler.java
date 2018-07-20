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

import com.github.yeriomin.yalpstore.bugreport.BugReportService;

class YalpStoreUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Context context;

    public YalpStoreUncaughtExceptionHandler(Context context) {
        this.context = context;
    }

    @Override
    public void uncaughtException(Thread t, final Throwable e) {
        e.printStackTrace();
        Intent errorIntent = new Intent(context.getApplicationContext(), BugReportActivity.class);
        errorIntent.putExtra(BugReportService.INTENT_STACKTRACE, getStackTraceString(e));
        errorIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.getApplicationContext().startActivity(errorIntent);
        System.exit(0);
    }

    private String getStackTraceString(Throwable e) {
        StringBuilder stackTrace = new StringBuilder();
        Throwable ex = e;
        while (null != ex) {
            stackTrace
                .append(ex.getClass().getName())
                .append(": \"")
                .append(ex.getMessage())
                .append("\"\n")
            ;
            for (StackTraceElement element: ex.getStackTrace()) {
                stackTrace
                    .append("\tat ")
                    .append(element.getClassName())
                    .append(".")
                    .append(element.getMethodName())
                    .append("(")
                    .append(element.getFileName())
                    .append(":")
                    .append(element.getLineNumber())
                    .append(")\n")
                ;
            }
            ex = ex.getCause();
            if (null != ex) {
                stackTrace.append("Caused by: ");
            }
        }
        return stackTrace.toString();
    }
}
