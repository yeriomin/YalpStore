package com.github.yeriomin.yalpstore;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.config.ACRAConfiguration;
import org.acra.config.ACRAConfigurationException;
import org.acra.config.ConfigurationBuilder;

public class YalpStoreApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        try {
            ACRAConfiguration config = new ConfigurationBuilder(this)
                .setMailTo(getString(R.string.about_developer_email))
                .setReportingInteractionMode(ReportingInteractionMode.DIALOG)
                .setResDialogText(R.string.application_crashed)
                .setCustomReportContent(
                    ReportField.ANDROID_VERSION,
                    ReportField.STACK_TRACE_HASH,
                    ReportField.STACK_TRACE,
                    ReportField.BUILD,
                    ReportField.BUILD_CONFIG,
                    ReportField.DISPLAY,
                    ReportField.DEVICE_FEATURES
                )
                .build();
            ACRA.init(this, config);
        } catch (ACRAConfigurationException e) {
            Log.e(getClass().getName(), "Could not configure ACRA: " + e.getMessage());
        } catch (Throwable e) {
            Log.e(getClass().getName(), "Unknown problem with ACRA: " + e.getMessage());
        }
    }
}
