package com.github.yeriomin.yalpstore;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.github.yeriomin.yalpstore.task.ConvertToNormalTask;
import com.github.yeriomin.yalpstore.task.SystemRemountTask;
import com.github.yeriomin.yalpstore.task.UninstallSystemAppTask;

public class SystemRemountDialogBuilder extends AlertDialog.Builder {

    private SystemRemountTask primaryTask;

    public SystemRemountDialogBuilder(Context context) {
        super(context);
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
