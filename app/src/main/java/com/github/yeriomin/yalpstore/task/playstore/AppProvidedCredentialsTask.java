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

import android.app.Activity;

import com.github.yeriomin.yalpstore.view.CredentialsDialogBuilder;
import com.github.yeriomin.yalpstore.view.LoginDialogBuilder;

import java.io.IOException;

public abstract class AppProvidedCredentialsTask extends CheckCredentialsTask {

    abstract protected void payload() throws IOException;

    @Override
    protected CredentialsDialogBuilder getDialogBuilder() {
        return new LoginDialogBuilder((Activity) context);
    }

    @Override
    protected Void doInBackground(String[] params) {
        try {
            payload();
        } catch (IOException e) {
            exception = e;
        }
        return null;
    }
}
