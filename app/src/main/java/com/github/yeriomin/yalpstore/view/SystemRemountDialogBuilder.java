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

import android.app.Activity;
import android.content.DialogInterface;

import com.github.yeriomin.yalpstore.BuildConfig;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.task.ConvertToNormalTask;
import com.github.yeriomin.yalpstore.task.SystemRemountTask;
import com.github.yeriomin.yalpstore.task.UninstallSystemAppTask;

public class SystemRemountDialogBuilder extends DialogWrapper {

    private SystemRemountTask primaryTask;

    public SystemRemountDialogBuilder(Activity activity) {
        super(activity);
        setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                primaryTask.execute();
                dialog.dismiss();
            }
        });
        setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    public SystemRemountDialogBuilder setPrimaryTask(SystemRemountTask primaryTask) {
        this.primaryTask = primaryTask;
        setMessage(getMessageId());
        setTitle(getTitleId());
        return this;
    }

    private int getMessageId() {
        if (primaryTask instanceof ConvertToNormalTask) {
            return R.string.dialog_message_system_app_warning_to_normal;
        } else if (primaryTask instanceof UninstallSystemAppTask) {
            return R.string.dialog_message_system_app_warning_uninstall;
        }
        return primaryTask.getApp().getPackageName().equals(BuildConfig.APPLICATION_ID)
            ? R.string.dialog_message_system_app_self
            : R.string.dialog_message_system_app_warning_to_system
        ;
    }

    private int getTitleId() {
        return primaryTask.getApp().getPackageName().equals(BuildConfig.APPLICATION_ID)
            ? R.string.dialog_title_system_app_self
            : R.string.dialog_title_system_app_warning
        ;
    }
}
