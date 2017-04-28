package com.github.yeriomin.yalpstore;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class SystemRemountDialogBuilder extends AlertDialog.Builder {

    private CheckShellTask checkTask;

    public SystemRemountDialogBuilder(Context context) {
        super(context);
        checkTask = new CheckShellTask(context);
        setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkTask.execute();
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
        checkTask.setPrimaryTask(primaryTask);
        return this;
    }
}
