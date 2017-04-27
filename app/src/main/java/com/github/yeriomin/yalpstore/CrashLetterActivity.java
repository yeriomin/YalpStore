package com.github.yeriomin.yalpstore;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.ClipboardManager;

public class CrashLetterActivity extends Activity {

    static public void send(Context activity, String letter) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.fromParts("mailto", activity.getString(R.string.about_developer_email), null));
        emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, activity.getPackageName() + " Crash Report");
        emailIntent.putExtra(Intent.EXTRA_TEXT, letter);
        if (emailIntent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(emailIntent);
        } else {
            ((ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE)).setText(letter);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showCrashDialog();
    }

    private AlertDialog showCrashDialog() {
        return new AlertDialog.Builder(this)
            .setTitle(getString(R.string.dialog_title_application_crashed))
            .setMessage(getString(R.string.dialog_message_application_crashed))
            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            })
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    send(
                        CrashLetterActivity.this,
                        getIntent().getExtras().getString(YalpStoreUncaughtExceptionHandler.INTENT_MESSAGE)
                    );
                    dialog.dismiss();
                    finish();
                }
            })
            .show()
        ;
    }
}
