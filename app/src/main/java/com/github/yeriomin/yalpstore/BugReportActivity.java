package com.github.yeriomin.yalpstore;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.bugreport.BugReportService;

public class BugReportActivity extends Activity {

    private String stackTrace;
    private boolean triggeredByCrash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bugreport_activity_layout);
        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        stackTrace = intent.getStringExtra(BugReportService.INTENT_STACKTRACE);
        triggeredByCrash = !TextUtils.isEmpty(stackTrace);
        setTitle(triggeredByCrash ? R.string.dialog_title_application_crashed : R.string.action_bug_report);
        ((TextView) findViewById(R.id.explanation)).setText(triggeredByCrash ? R.string.bug_report_explanation_crash : R.string.bug_report_explanation_bug_report);
        if (!PreferenceActivity.getBoolean(this, PlayStoreApiAuthenticator.PREFERENCE_APP_PROVIDED_EMAIL)) {
            ((EditText) findViewById(R.id.identification)).setText(PreferenceActivity.getString(this, PlayStoreApiAuthenticator.PREFERENCE_EMAIL));
        }
    }

    public void sendBugReport(View view) {
        ContextUtil.toastShort(getApplicationContext(), getString(R.string.thank_you));
        startService(getBugReportIntent(
            stackTrace,
            ((EditText) findViewById(R.id.message)).getText().toString(),
            ((EditText) findViewById(R.id.identification)).getText().toString()
        ));
        finishAndGoToHomeScreen(view);
    }

    public void finishAndGoToHomeScreen(View view) {
        finish();
        if (triggeredByCrash) {
            goToHomeScreen();
        }
    }

    private void goToHomeScreen() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // No home screen?
        }
    }

    private Intent getBugReportIntent(String stackTrace, String message, String identification) {
        Intent intentBugReport = new Intent(getApplicationContext(), BugReportService.class);
        intentBugReport.setAction(BugReportService.ACTION_SEND_FTP);
        intentBugReport.putExtra(BugReportService.INTENT_STACKTRACE, stackTrace);
        intentBugReport.putExtra(BugReportService.INTENT_MESSAGE, message);
        intentBugReport.putExtra(BugReportService.INTENT_IDENTIFICATION, identification);
        return intentBugReport;
    }
}
